<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="반품신청일"
        label-position="left"
        class="criteria date"
      />
      <AppSelect
        v-model="criteria.criteria"
        label="검색조건"
        label-position="left"
        :options="searchOptions"
        :initial-value="criteria.criteria"
      />
      <AppInputText v-model="criteria.keyword" />
    </SearchArea>

    <AppTabs v-model="activeTab" :tab-items="tabItems" />

    <AppTable
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      :paginated-data="paginatedReturnList"
      show-excel-export
      @reload="onReload"
      @change-page="onChangePage"
      @export-excel="onExportExcel"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppTabs from '@/components/common/AppTabs.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import HQReturnApi from '@/utils/api/HQReturnApi';
import { CRITERIA_HQ_RETURN_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import {
  formatKoApprovalStatus,
  formatKoReturnReason,
  formatKoReturnStatus,
  formatKoSearchCriteria,
} from '@/utils/format';
import { getReturnStatusSeverity, makeSelectOption, makeTabs } from '@/utils/helper';

const router = useRouter();

const paginatedReturnList = ref([]);
const page = ref(0);
const pageSize = ref(15);
const totalElements = ref(0);
const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  criteria: SEARCH_CRITERIA.RETURN_CODE,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const searchOptions = computed(() => {
  return CRITERIA_HQ_RETURN_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const TAB_ITEM = {
  ALL: 'ALL',
  UNCONFIRMED: 'UNCONFIRMED',
};
function formatKoTabItem(tabValue) {
  if (tabValue === TAB_ITEM.ALL) return '전체 반품';
  if (tabValue === TAB_ITEM.UNCONFIRMED) return '미결재 반품';
  return 'Tab';
}
const activeTab = ref(TAB_ITEM.ALL);
const tabItems = computed(() => {
  return [TAB_ITEM.ALL, TAB_ITEM.UNCONFIRMED].map(e => makeTabs(formatKoTabItem(e), e));
});

const hqReturnApi = new HQReturnApi();

const getReturnList = () => {
  hqReturnApi
    .getReturnList({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
      getConfirmed: activeTab.value === TAB_ITEM.UNCONFIRMED,
    })
    .then(data => {
      paginatedReturnList.value = data.content;
      totalElements.value = data.totalElements;
    });
};

const columns = [
  { field: 'returningCode', header: '반품코드' },
  { field: 'franchiseName', header: '반품요청지점' },
  { field: 'itemName', header: '반품상품' },
  { field: 'reason', header: '반품사유', render: data => formatKoReturnReason(data.reason) },
  {
    field: 'status',
    header: '반품상태',
    render: data => formatKoReturnStatus(data.status),
    template: {
      tag: {
        getSeverity: data => getReturnStatusSeverity(data.status),
      },
    },
  },
  { field: 'approvalStatus', header: '결재상태', render: data => formatKoApprovalStatus(data.approvalStatus) },
  { field: 'memberCode', header: '반품담당자' },
  { field: 'createdAt', header: '반품요청일' },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: data => {
            router.push({ name: 'hq:order:return:detail', params: { returnCode: data.returningCode } });
          },
        },
      ],
    },
  },
];

const onReload = () => {
  getReturnList();
};

const onSearch = () => {
  getReturnList();
};

const onReset = () => {
  page.value = 0;
  criteria.value = getInitialCriteria();
  getReturnList();
};

const onChangePage = event => {
  page.value = event.page;
  getReturnList();
};

const onExportExcel = () => {
  hqReturnApi
    .getAllReturnList({
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
      getConfirmed: activeTab.value === TAB_ITEM.UNCONFIRMED,
    })
    .then(rows => {
      const orderedFields = columns.filter(e => e.field).map(e => e.field);
      const headerNames = columns.filter(e => e.field).map(e => e.header);
      const tableRows = rows.map(row => ({
        ...row,
        reason: formatKoReturnReason(row.reason),
        status: formatKoReturnStatus(row.status),
        approvalStatus: formatKoApprovalStatus(row.approvalStatus),
      }));

      const excelManager = new ExcelManager(tableRows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.export(`반품요청목록`);
    });
};

onMounted(() => {
  getReturnList();
});

// 탭이 변경되면 API 호출
watch(activeTab, newActiveTab => {
  getReturnList();
});
</script>

<style scoped>
.criteria.date {
  grid-column: 1 / 7;
}
</style>
