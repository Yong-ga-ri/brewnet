<template>
  <div class="purchase-stock-arrival-list-container">
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="발주일자"
        class="criteria date"
        label-position="left"
      />
      <AppSelect
        v-model="criteria.criteria"
        :options="criteriaOptions"
        :initial-value="criteria.criteria"
        label="검색어"
        label-position="left"
        full-width
      />
      <AppInputText v-model="criteria.keyword" placeholder="검색어 입력" class="criteria keyword" />
    </SearchArea>

    <AppTabs v-model="activeTab" :tab-items="tabItems" />

    <AppTable
      :columns="columns"
      :rows-per-page="pageSize"
      :paginated-data="paginatedItems"
      :total-elements="totalElements"
      @reload="onReload"
      @change-page="onChangePage"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { computed, onMounted, ref, watch } from 'vue';

import AppTable from '@/components/common/AppTable.vue';
import AppTabs from '@/components/common/AppTabs.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import { CRITERIA_IN_STOCK, SEARCH_CRITERIA } from '@/utils/constant';
import { formatKoSearchCriteria } from '@/utils/format';
import { makeSelectOption, makeTabs } from '@/utils/helper';

const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  criteria: SEARCH_CRITERIA.ITEM_UNIQUE_CODE,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const paginatedItems = ref([]);
const totalElements = ref(0);
const pageSize = ref(15);
const page = ref(1);

const TAB_ITEM = {
  ALL: 'ALL',
  UNCHECK: 'UNCHECK',
};
function formatKoTabItem(tabValue) {
  if (tabValue === TAB_ITEM.ALL) return '전체 입고내역';
  if (tabValue === TAB_ITEM.UNCHECK) return '미확인 입고내역';
  return 'Tab';
}
const activeTab = ref(TAB_ITEM.UNCHECK);

const criteriaOptions = computed(() => {
  return CRITERIA_IN_STOCK.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});
const tabItems = computed(() => {
  return [TAB_ITEM.UNCHECK, TAB_ITEM.ALL].map(e => makeTabs(formatKoTabItem(e), e));
});

const hqPurchaseApi = new HQPurchaseApi();

const onStockUncheckToCheck = data => {
  hqPurchaseApi.stockIn({ itemCode: data.itemCode, purchaseCode: data.purchaseCode }).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '입고처리되었습니다.', life: 3000 });
    onReload();
  });
};
const handleStockIn = data => {
  showConfirm({
    header: '입고 진행',
    message: '입고 완료 처리하시겠습니까?',
    acceptLabel: '입고 진행',
    onAccept: () => onStockUncheckToCheck(data),
  });
};

const columns = [
  {
    field: 'purchaseCode',
    header: '발주코드',
  },
  {
    field: 'itemUniqueCode',
    header: '상품코드',
  },
  {
    field: 'itemName',
    header: '상품명',
  },
  { field: 'quantity', header: '입고수량', render: data => data.quantity.toLocaleString() },
  { field: 'storageName', header: '입고창고' },
  { field: 'correspondentName', header: '거래처' },
  { field: 'createdAt', header: '발주일자' },
  {
    field: '',
    header: '입고처리',
    template: {
      button: [
        {
          getLabel: data => {
            if (data.storageConfirmed) {
              return '입고완료';
            }
            return '입고확인';
          },
          clickHandler: handleStockIn,
          getDisabled: data => data.storageConfirmed,
          getSeverity: data => {
            if (data.storageConfirmed) return 'secondary';
            return undefined;
          },
        },
      ],
    },
  },
];

const getInStockItems = () => {
  const currentCriteria = criteria.value.criteria;
  const queryKeyword = criteria.value.keyword;

  hqPurchaseApi
    .getInStockItems({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      // 현재 선택된 검색기준을 확인하여 query params 설정
      itemUniqueCode: currentCriteria === SEARCH_CRITERIA.ITEM_UNIQUE_CODE ? queryKeyword : undefined,
      itemName: currentCriteria === SEARCH_CRITERIA.ITEM_NAME ? queryKeyword : undefined,
      correspondentName: currentCriteria === SEARCH_CRITERIA.CORRESPONDENT_NAME ? queryKeyword : undefined,
      storageName: currentCriteria === SEARCH_CRITERIA.STORAGE_NAME ? queryKeyword : undefined,
      onlyUnchecked: activeTab.value === TAB_ITEM.UNCHECK,
    })
    .then(data => {
      paginatedItems.value = data.data;
      totalElements.value = data.totalCount;
    });
};

const onSearch = () => {
  getInStockItems();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  getInStockItems();
};

const onReload = () => {
  getInStockItems();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getInStockItems();
};

onMounted(() => {
  getInStockItems();
});

// 탭이 변경되면 API 호출
watch(activeTab, newActiveTab => {
  getInStockItems();
});
</script>

<style scoped>
.purchase-stock-arrival-list-container {
  .criteria.date {
    grid-column: 1 / 7;
  }

  .criteria.keyword {
    grid-column: 2 / 4;
  }
}
</style>
