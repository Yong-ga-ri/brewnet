<template>
  <div>
    <!-- 검색 area -->
    <SearchArea grid class="exchange-search" @search="onSearch" @form-reset="onReset">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="작성일자"
        class="criteria created-at"
      />
      <AppSelect
        v-model="criteria.criteria"
        label="검색조건"
        :options="searchOptions"
        :initial-value="criteria.criteria"
      />
      <AppInputText id="input_name_keyword" v-model="criteria.keyword" label="검색어" />
    </SearchArea>

    <AppTable
      :paginated-data="paginatedExchanges"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="size"
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
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useUserStore } from '@/stores/user';
import FCExchangeApi from '@/utils/api/FCExchangeApi';
import { CRITERIA_FC_EXCHANGE_LIST, EXCHANGE_STATUS, SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { formatKoExchangeStatus, formatKoSearchCriteria } from '@/utils/format';
import { getExchangeStatusSeverity, makeSelectOption } from '@/utils/helper';

const router = useRouter();
const toast = useToast();

const totalElements = ref(0);
const page = ref(0);
const size = ref(15);

const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  criteria: SEARCH_CRITERIA.EXCHANGE_CODE,
  keyword: '',
});

const criteria = ref(getInitialCriteria());
const paginatedExchanges = ref([]);
const fcExchangeApi = new FCExchangeApi();
const searchFilter = ref('exchangeCode');
const searchOptions = computed(() => {
  return CRITERIA_FC_EXCHANGE_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const columns = [
  {
    field: 'status',
    header: '주문상태',
    render: data => formatKoExchangeStatus(data.status),
    template: {
      tag: {
        getSeverity: data => getExchangeStatusSeverity(data.status),
      },
    },
  },
  { field: 'exchangeCode', header: '주문번호' },
  { field: 'itemName', header: '주문상품명' },
  {
    field: 'sumPrice',
    header: '주문금액',
    render: data => {
      return (data.sumPrice * 1.1).toLocaleString();
    },
  },
  { field: 'createdAt', header: '주문일자' },
  { field: 'completedAt', header: '완료일자' },
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

function clickGoDetail(data) {
  router.push({ name: 'fc:home:exchange:detail', params: { exchangeCode: data.exchangeCode } });
}

const getExchanges = () => {
  fcExchangeApi
    .searchExchanges({
      page: page.value,
      size: size.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
    })
    .then(data => {
      totalElements.value = data.totalElements;
      paginatedExchanges.value = data.content;
    });
};

const onSearch = () => {
  getExchanges();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 0;
  getExchanges();
};

const reloadData = () => {
  getExchanges();
};

const onChangePage = event => {
  page.value = event.page;
  getExchanges();
};

onMounted(() => {
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
