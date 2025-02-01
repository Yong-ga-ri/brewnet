<template>
  <div>
    <!-- 검색 area -->
    <SearchArea grid class="exchange-search" @search="onSearch" @form-reset="onReset">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="요청일자"
        class="criteria created-at"
        label-position="left"
      />
      <AppSelect
        v-model="criteria.criteria"
        label="검색조건"
        :options="searchOptions"
        :initial-value="criteria.criteria"
        label-position="left"
      />
      <AppInputText id="input_name_keyword" v-model="criteria.keyword" placeholder="검색어를 입력해주세요" />
    </SearchArea>

    <AppTabs v-model="activeTab" :tab-items="tabItems" />

    <AppTable
      :paginated-data="paginatedExchanges"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="size"
      show-excel-export
      @reload="reloadData"
      @change-page="onChangePage"
      @export-excel="onExportExcel"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { ref, onMounted, computed, watch, defineAsyncComponent } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppTabs from '@/components/common/AppTabs.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import HQExchangeApi from '@/utils/api/HQExchangeApi';
import { CRITERIA_HQ_EXCHANGE_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { formatKoExchangeReason, formatKoExchangeStatus, formatKoSearchCriteria } from '@/utils/format';
import { getExchangeStatusSeverity, makeSelectOption, makeTabs } from '@/utils/helper';

const router = useRouter();
const toast = useToast();

const totalElements = ref(0);
const page = ref(0);
const size = ref(15);

const paginatedExchanges = ref([]);

const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  criteria: SEARCH_CRITERIA.EXCHANGE_CODE,
  keyword: '',
});

const criteria = ref(getInitialCriteria());
const hqExchangeApi = new HQExchangeApi();
const searchOptions = computed(() => {
  return CRITERIA_HQ_EXCHANGE_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const TAB_ITEM = {
  ALL: 'ALL',
  UNCONFIRMED: 'UNCONFIRMED',
};
function formatKoTabItem(tabValue) {
  if (tabValue === TAB_ITEM.ALL) return '전체 교환';
  if (tabValue === TAB_ITEM.UNCONFIRMED) return '미결재 교환';
  return 'Tab';
}
const activeTab = ref(TAB_ITEM.ALL);
const tabItems = computed(() => {
  return [TAB_ITEM.ALL, TAB_ITEM.UNCONFIRMED].map(e => makeTabs(formatKoTabItem(e), e));
});

function clickGoDetail(data) {
  router.push({ name: 'hq:order:exchange:detail', params: { exchangeCode: data.exchangeCode } });
}

const columns = [
  { field: 'exchangeCode', header: '교환번호', sortable: true },
  { field: 'franchiseName', header: '교환요청지점' },
  { field: 'itemName', header: '교환상품' },
  {
    field: 'reason',
    header: '교환사유',
    render: data => formatKoExchangeReason(data.reason),
  },
  {
    field: 'status',
    header: '교환상태',
    render: data => formatKoExchangeStatus(data.status),
    template: {
      tag: {
        getSeverity: data => getExchangeStatusSeverity(data.status),
      },
    },
  },
  { field: 'memberCode', header: '교환담당자' },
  { field: 'createdAt', header: '교환요청일' },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: clickGoDetail,
        },
      ],
    },
  },
];

const getExchanges = () => {
  hqExchangeApi
    .searchExchanges({
      page: page.value,
      size: size.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
      getConfirmed: activeTab.value === TAB_ITEM.UNCONFIRMED,
    })
    .then(data => {
      totalElements.value = data.totalElements;
      paginatedExchanges.value = data.content;
    });
};

const reloadData = () => {
  getExchanges();
};

const onChangePage = event => {
  page.value = event.page;
  getExchanges();
};

const onSearch = () => {
  getExchanges();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 0;
  getExchanges();
};

const onExportExcel = () => {
  hqExchangeApi
    .getExchangesExcelData({
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
      getConfirmed: activeTab.value === TAB_ITEM.UNCONFIRMED,
    })
    .then(rows => {
      const orderedFields = columns.filter(e => e.field).map(e => e.field); // 엑셀 컬럼 순서
      const headerNames = columns.filter(e => e.field).map(e => e.header); // 헤더명

      const tableRows = rows.map(row => ({
        ...row,
        status: formatKoExchangeStatus(row.status),
        reason: formatKoExchangeReason(row.reason),
      }));

      const excelManager = new ExcelManager(tableRows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.export(`교환목록${dayjs().format('YYMMDD')}`);
    })
    .catch(e => {
      toast.add({ severity: 'error', summary: '처리 실패', detail: e.message, life: 3000 });
    });
};

onMounted(() => {
  getExchanges();
});

// 탭이 변경되면 API 호출
watch(activeTab, newActiveTab => {
  getExchanges();
});
</script>

<style scoped>
.exchange-search {
  .criteria.created-at {
    grid-column: 1 / 7;
  }
}
</style>
