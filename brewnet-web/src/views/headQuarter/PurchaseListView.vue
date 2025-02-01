<template>
  <div class="purchase-list-container">
    <SearchArea grid class="purchase-search" @search="onSearch" @form-reset="onReset">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="작성일자"
        class="criteria created-at"
        label-position="left"
      />
      <AppInputText v-model="criteria.purchaseCodeKeyword" label="발주코드" />
      <AppInputText v-model="criteria.purchaseMemberKeyword" label="기안자명" />
      <AppInputText v-model="criteria.supplierKeyword" label="거래처명" />
      <AppInputText v-model="criteria.storageKeyword" label="창고명" />
    </SearchArea>

    <AppTabs v-model="activeTab" :tab-items="tabItems" />

    <AppTable
      :columns="columns"
      :paginated-data="paginatedPurchases"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      @reload="reloadData"
      @change-page="onChangePage"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppTabs from '@/components/common/AppTabs.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import { APPROVAL_STATUS } from '@/utils/constant';
import { formatKoApprovalStatus } from '@/utils/format';
import { getApprovalStatusSeverity, makeTabs } from '@/utils/helper';
import LocalStorageUtil from '@/utils/localStorage';

const toast = useToast();
const router = useRouter();

const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  purchaseCodeKeyword: null,
  purchaseMemberKeyword: null,
  supplierKeyword: null,
  storageKeyword: null,
});

const criteria = ref(getInitialCriteria());
const paginatedPurchases = ref([]);
const totalElements = ref(0);
const page = ref(1);
const pageSize = ref(15);

const TAB_ITEM = {
  ALL: 'ALL',
  UNCONFIRMED: 'UNCONFIRMED',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
};
function formatKoTabItem(tabValue) {
  if (tabValue === TAB_ITEM.ALL) return '전체 발주';
  if (tabValue === TAB_ITEM.UNCONFIRMED) return '미결재 발주';
  if (tabValue === TAB_ITEM.APPROVED) return '결재승인 발주';
  if (tabValue === TAB_ITEM.REJECTED) return '결재반려 발주';
  return 'Tab';
}
const activeTab = ref(TAB_ITEM.ALL);
const tabItems = computed(() => {
  return [TAB_ITEM.ALL, TAB_ITEM.UNCONFIRMED, TAB_ITEM.APPROVED, TAB_ITEM.REJECTED].map(e =>
    makeTabs(formatKoTabItem(e), e),
  );
});

const localStorageUtil = new LocalStorageUtil();
const hqPurchaseApi = new HQPurchaseApi();

function clickSend(data) {
  localStorageUtil.saveSendCompletePurchase(data.purchaseCode);

  toast.add({
    severity: 'success',
    summary: '처리 성공',
    detail: '구매품의서가 회계부서로 전송되었습니다.',
    life: 3000,
  });

  // reload data
  reloadData();
}

function clickGoDetail(data) {
  router.push({ name: 'hq:purchase:detail', params: { purchaseCode: data.purchaseCode } });
}

const columns = [
  {
    field: 'approved',
    header: '결재상태',
    render: data => formatKoApprovalStatus(data.approved),
    template: {
      tag: {
        getSeverity: data => getApprovalStatusSeverity(data.approved),
      },
    },
  },
  {
    field: 'purchaseCode',
    header: '발주코드',
  },
  { field: 'correspondentName', header: '거래처명' },
  { field: 'sumPrice', header: '총 발주금액', alignment: 'right', render: data => data.sumPrice.toLocaleString() },
  { field: 'storageName', header: '창고명' },
  { field: 'memberName', header: '기안자' },
  { field: 'createdAt', header: '작성일자' },
  { field: 'approvedAt', header: '결재일자' },
  {
    field: '',
    header: '회계팀 전송',
    template: {
      button: [
        {
          getLabel: data => {
            if (localStorageUtil.isSendCompletePurchase(data.purchaseCode)) return '전송완료';
            return '구매품의서 전송';
          },
          clickHandler: clickSend,
          getDisabled: data => {
            // 이미 전송완료했거나 APPROVED 상태가 아니면 disabled
            return (
              localStorageUtil.isSendCompletePurchase(data.purchaseCode) || data.approved !== APPROVAL_STATUS.APPROVED
            );
          },
          getSeverity: data => {
            if (localStorageUtil.isSendCompletePurchase(data.purchaseCode)) return 'secondary';
            return undefined;
          },
        },
      ],
    },
  },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: data => '상세보기',
          clickHandler: clickGoDetail,
        },
      ],
    },
  },
];

const getPurchases = () => {
  hqPurchaseApi
    .getPurchases({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      purchaseCode: criteria.value.purchaseCodeKeyword,
      memberName: criteria.value.purchaseMemberKeyword,
      correspondentName: criteria.value.supplierKeyword,
      storageName: criteria.value.storageKeyword,
      approved: activeTab.value === TAB_ITEM.ALL ? undefined : activeTab.value,
    })
    .then(data => {
      totalElements.value = data.totalCount;
      paginatedPurchases.value = data.data;
    });
};

const reloadData = () => {
  getPurchases();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getPurchases();
};

const onSearch = () => {
  getPurchases();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  getPurchases();
};

onMounted(() => {
  getPurchases();
});

// 탭이 변경되면 API 호출
watch(activeTab, newActiveTab => {
  getPurchases();
});
</script>

<style scoped>
.purchase-search {
  .criteria.created-at {
    grid-column: 1 / 7;
  }
}
</style>
