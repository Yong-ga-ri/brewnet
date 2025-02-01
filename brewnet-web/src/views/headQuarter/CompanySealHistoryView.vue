<template>
  <div class="company-seal-history-container">
    <SearchArea @form-reset="onReset" @search="onSearch">
      <AppDateRangePicker v-model:start="criteria.startDate" v-model:end="criteria.endDate" label="사용일자" />
    </SearchArea>

    <AppTable
      :paginated-data="paginatedHistories"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      @change-page="onChangePage"
      @reload="reload"
      @change-sort="onChangeSort"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { onMounted, ref } from 'vue';

import AppTable from '@/components/common/AppTable.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import CompanyApi from '@/utils/api/CompanyApi';
import { SORTING_OPTION } from '@/utils/constant';

const page = ref(0);
const paginatedHistories = ref([]);
const pageSize = ref(15);
const totalElements = ref(0);
const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
});
const criteria = ref(getInitialCriteria());
const sorting = ref(null);

const companyApi = new CompanyApi();

const getCompanySealHistory = () => {
  companyApi
    .getCompanySealHistory({
      page: page.value,
      pageSize: pageSize.value,
      sort: sorting.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
    })
    .then(data => {
      paginatedHistories.value = data.content;
      totalElements.value = data.totalElements;
    });
};

const columns = [
  { field: 'orderPrintCode', header: '출력코드' },
  { field: 'memberName', header: '임직원명', sortable: true },
  { field: 'reason', header: '사유' },
  { field: 'printedAt', header: '사용일자' },
  {
    field: '',
    header: '서명',
    template: {
      image: {
        getSrc: data => data.memberSignature || '',
      },
    },
  },
];

const onChangePage = event => {
  page.value = event.page;
  getCompanySealHistory();
};

const reload = () => {
  getCompanySealHistory();
};

const onSearch = () => {
  getCompanySealHistory();
};

const onReset = () => {
  page.value = 0;
  criteria.value = getInitialCriteria();
  sorting.value = null;

  getCompanySealHistory();
};

const onChangeSort = (field, order) => {
  if (!field || !order) return;

  if (field === 'name') {
    if (order === 1) {
      sorting.value = SORTING_OPTION.NAME_ASC;
    } else if (order === -1) {
      sorting.value = SORTING_OPTION.NAME_DESC;
    }
  }

  getCompanySealHistory();
};

onMounted(() => {
  getCompanySealHistory();
});
</script>

<style scoped></style>
