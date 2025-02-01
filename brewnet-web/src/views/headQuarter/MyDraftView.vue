<template>
  <div class="my-draft-container">
    <SearchArea grid @search="onSearch" @form-reset="onReset">
      <AppDateRangePicker
        v-model:start="criteria.startDate"
        v-model:end="criteria.endDate"
        label="기안일자"
        label-position="left"
        class="criteria date"
      />
      <AppRadioList
        v-model="criteria.approval"
        name="search-area-approval"
        :options="approvalOptions"
        label="결재상태"
        class="criteria radio"
        label-position="left"
      />
    </SearchArea>

    <AppTable
      v-model:sorting="sorting"
      :columns="columns"
      :rows-per-page="pageSize"
      :paginated-data="paginatedDrafts"
      :total-elements="totalElements"
      :sorting-options="sortingOptions"
      @reload="onReload"
      @change-page="onChangePage"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppDateRangePicker from '@/components/common/form/AppDateRangePicker.vue';
import AppRadioList from '@/components/common/form/AppRadioList.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useDraftNavigation } from '@/hooks/useDraftNavigation';
import MemberApi from '@/utils/api/MemberApi';
import { APPROVAL_STATUS_LIST } from '@/utils/constant';
import { formatKoApprovalStatus, formatKoDraftKind } from '@/utils/format';
import { makeRadioOption, makeSelectOption } from '@/utils/helper';

const { clickGoDetail } = useDraftNavigation();

const APPROVAL_ALL = 'ALL';
const APPROVAL_OPTIONS = [APPROVAL_ALL].concat(APPROVAL_STATUS_LIST);

const SORTING_OPTION = {
  DATE_ASC: 'DATE_ASC',
  DATE_DESC: 'DATE_DESC',
};
const SORTING_OPTIONS = [SORTING_OPTION.DATE_ASC, SORTING_OPTION.DATE_DESC];
const formatKoSortingOption = opt => {
  if (opt === SORTING_OPTION.DATE_ASC) return '기안일 오래된 순';
  if (opt === SORTING_OPTION.DATE_DESC) return '기안일 최근 순';
  return '';
};

const getInitialCriteria = () => ({
  startDate: dayjs().subtract(1, 'year').toDate(),
  endDate: new Date(),
  approval: APPROVAL_ALL,
});
const criteria = ref(getInitialCriteria());
const sorting = ref(SORTING_OPTION.DATE_ASC);
const approvalOptions = computed(() => {
  return APPROVAL_OPTIONS.map(e => makeRadioOption(e === APPROVAL_ALL ? '전체' : formatKoApprovalStatus(e), e, e));
});
const sortingOptions = computed(() => {
  return SORTING_OPTIONS.map(e => makeSelectOption(formatKoSortingOption(e), e));
});
const page = ref(0);
const pageSize = ref(15);
const paginatedDrafts = ref([]);
const totalElements = ref(0);

const memberApi = new MemberApi();

const columns = [
  { field: 'kind', header: '구분', render: data => formatKoDraftKind(data.kind) },
  {
    field: 'code',
    header: '구분코드',
  },
  {
    field: 'title',
    header: '문서 제목',
  },
  { field: 'drafterName', header: '기안자' },
  { field: 'date', header: '기안일' },
  {
    field: 'status',
    header: '결재상태',
    render: data => formatKoApprovalStatus(data.status),
  },
  { field: 'approverName', header: '결재자' },
  {
    field: '',
    header: '기안서 보기',
    template: {
      button: [{ getLabel: () => '상세보기', clickHandler: clickGoDetail }],
    },
  },
];

const getMyDrafts = () => {
  memberApi
    .getMyDrafts({
      page: page.value,
      pageSize: pageSize.value,
      startDate: criteria.value.startDate,
      endDate: criteria.value.endDate,
      approval: criteria.value.approval === APPROVAL_ALL ? undefined : criteria.value.approval,
      dateSort: sorting.value === SORTING_OPTION.DATE_ASC ? 'ASC' : 'DESC',
    })
    .then(data => {
      paginatedDrafts.value = data.content;
      totalElements.value = data.totalElements;
    });
};

const onSearch = () => {
  getMyDrafts();
};

const onReset = () => {
  page.value = 0;
  criteria.value = getInitialCriteria();
  getMyDrafts();
};

const onChangePage = event => {
  page.value = event.page;
  getMyDrafts();
};

const onReload = () => {
  getMyDrafts();
};

onMounted(() => {
  getMyDrafts();
});

// 정렬기준이 바뀌면 API 호출
watch(sorting, () => {
  getMyDrafts();
});
</script>

<style scoped>
.my-draft-container {
  .criteria.date {
    grid-column: 1 / 7;
  }

  .criteria.radio {
    grid-column: 1 / 4;
  }
}
</style>
