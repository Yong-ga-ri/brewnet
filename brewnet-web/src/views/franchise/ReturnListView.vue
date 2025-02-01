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

    <AppTable
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      :paginated-data="paginatedReturnList"
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
import FCReturnApi from '@/utils/api/FCReturnApi';
import { CRITERIA_FC_RETURN_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import { formatKoReturnStatus, formatKoSearchCriteria } from '@/utils/format';
import { getReturnStatusSeverity, makeSelectOption } from '@/utils/helper';

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
  return CRITERIA_FC_RETURN_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const fcReturnApi = new FCReturnApi();

const getReturnList = () => {
  fcReturnApi
    .getReturnList({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      criteria: criteria.value.criteria,
      keyword: criteria.value.keyword,
    })
    .then(data => {
      paginatedReturnList.value = data.content;
      totalElements.value = data.totalElements;
    });
};

const columns = [
  {
    field: 'status',
    header: '상태',
    render: data => formatKoReturnStatus(data.status),
    template: {
      tag: {
        getSeverity: data => getReturnStatusSeverity(data.status),
      },
    },
  },
  { field: 'orderCode', header: '주문코드' },
  { field: 'returningCode', header: '반품코드' },
  { field: 'itemName', header: '주문상품명' },
  { field: 'sumPrice', header: '주문금액', render: data => data.sumPrice.toLocaleString() },
  { field: 'createdAt', header: '반품신청일자' },
  { field: 'completedAt', header: '완료일자' },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: data => {
            router.push({ name: 'fc:home:return:detail', params: { returnCode: data.returningCode } });
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

onMounted(() => {
  getReturnList();
});
</script>

<style scoped>
.criteria.date {
  grid-column: 1 / 7;
}
</style>
