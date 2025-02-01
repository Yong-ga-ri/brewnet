<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="처리일자"
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

    <AppTable
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      :paginated-data="paginatedReturnStockDeptList"
      @reload="onReload"
      @change-page="onChangePage"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import HQReturnApi from '@/utils/api/HQReturnApi';
import { CRITERIA_RETURN_STOCK_DEPT_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import { formatKoOtherDeptCheckStatus, formatKoReturnStockStatus, formatKoSearchCriteria } from '@/utils/format';
import { getReturnStockCheckStatusSeverity, makeSelectOption } from '@/utils/helper';

const router = useRouter();

const paginatedReturnStockDeptList = ref([]);
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
  return CRITERIA_RETURN_STOCK_DEPT_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const hqReturnApi = new HQReturnApi();

const getReturnStockDeptList = () => {
  hqReturnApi
    .getStockDeptList({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
    })
    .then(data => {
      paginatedReturnStockDeptList.value = data.content;
      totalElements.value = data.totalElements;
    });
};

const columns = [
  { field: 'returningStockHistoryCode', header: '재고처리코드' },
  { field: 'status', header: '처리상태', render: data => formatKoReturnStockStatus(data.status) },
  { field: 'manager', header: '재고담당자' },
  { field: 'createdAt', header: '처리일자' },
  {
    field: 'confirmed',
    header: '확인여부',
    render: data => formatKoOtherDeptCheckStatus(data.confirmed),
    template: {
      tag: {
        getSeverity: data => getReturnStockCheckStatusSeverity(data.confirmed),
      },
    },
  },
  { field: 'returningCode', header: '반품코드' },
  { field: 'returningManager', header: '반품담당자' },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: data => {
            router.push({
              name: 'hq:order:return:stock-detail',
              params: { detailCode: data.returningStockHistoryCode },
            });
          },
        },
      ],
    },
  },
];

const onReload = () => {
  getReturnStockDeptList();
};

const onSearch = () => {
  getReturnStockDeptList();
};

const onReset = () => {
  page.value = 0;
  criteria.value = getInitialCriteria();
  getReturnStockDeptList();
};

const onChangePage = event => {
  page.value = event.page;
  getReturnStockDeptList();
};

onMounted(() => {
  getReturnStockDeptList();
});
</script>

<style scoped>
.criteria.date {
  grid-column: 1 / 7;
}
</style>
