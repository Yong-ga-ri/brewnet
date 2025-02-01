<template>
  <AppModalBody>
    <SearchArea grid @search="onSearch" @form-reset="onReset">
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

    <AppTable
      :columns="columns"
      :paginated-data="paginatedItems"
      :rows-per-page="pageSize"
      :total-elements="totalElements"
      show-excel-export
      @reload="onReload"
      @change-page="onChangePage"
      @export-excel="onExportExcel"
    />
  </AppModalBody>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, inject, onMounted, ref } from 'vue';

import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';
import { CRITERIA_CORRESPONDENT_ITEM_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { formatKoSearchCriteria } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

import AppModalBody from '../common/AppModalBody.vue';
import AppTable from '../common/AppTable.vue';
import AppInputText from '../common/form/AppInputText.vue';
import AppSelect from '../common/form/AppSelect.vue';
import SearchArea from '../common/SearchArea.vue';

const dialogRef = inject('dialogRef');
const currentCorrespondentCode = ref(null);

const page = ref(1);
const pageSize = ref(15);
const paginatedItems = ref([]);
const totalElements = ref(0);

const getInitialCriteria = () => ({
  criteria: SEARCH_CRITERIA.ITEM_UNIQUE_CODE,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const criteriaOptions = computed(() => {
  return CRITERIA_CORRESPONDENT_ITEM_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const hqCorrespondentApi = new HQCorrespondentApi();

const columns = [
  {
    field: 'categoryName',
    header: '카테고리',
  },
  {
    field: 'itemUniqueCode',
    header: '상품코드',
  },
  {
    field: 'itemName',
    header: '상품명',
  },
  {
    field: 'sellingPrice',
    header: '판매단가',
    render: data => data.sellingPrice.toLocaleString(),
    alignment: 'right',
  },
  {
    field: 'purchasePrice',
    header: '구매단가',
    render: data => data.purchasePrice.toLocaleString(),
    alignment: 'right',
  },
  {
    field: 'safetyStock',
    header: '안전재고값',
    render: data => data.safetyStock.toLocaleString(),
    alignment: 'right',
  },
  {
    field: 'active',
    header: '상태',
    render: data => {
      if (data.active) return '활성';
      return '비활성';
    },
    template: {
      tag: {
        getSeverity: data => (data.active ? undefined : 'secondary'),
      },
    },
  },
];

const getCorrespondentItems = () => {
  hqCorrespondentApi
    .getCorrespondentItems({
      page: page.value,
      pageSize: pageSize.value,
      correspondentCode: currentCorrespondentCode.value,
      itemUniqueCode: criteria.value.criteria === SEARCH_CRITERIA.ITEM_UNIQUE_CODE ? criteria.value.keyword : undefined,
      itemName: criteria.value.criteria === SEARCH_CRITERIA.ITEM_NAME ? criteria.value.keyword : undefined,
    })
    .then(data => {
      paginatedItems.value = data.data;
      totalElements.value = data.totalCount;
    });
};

const onSearch = () => {
  getCorrespondentItems();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 1;
  getCorrespondentItems();
};

const onReload = () => {
  getCorrespondentItems();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getCorrespondentItems();
};

const onExportExcel = () => {
  hqCorrespondentApi
    .getAllCorrespondentItemList({
      correspondentCode: currentCorrespondentCode.value,
      itemUniqueCode: criteria.value.criteria === SEARCH_CRITERIA.ITEM_UNIQUE_CODE ? criteria.value.keyword : undefined,
      itemName: criteria.value.criteria === SEARCH_CRITERIA.ITEM_NAME ? criteria.value.keyword : undefined,
    })
    .then(rows => {
      const orderedFields = columns.filter(e => e.field).map(e => e.field);
      const headerNames = columns.filter(e => e.field).map(e => e.header);
      const tableRows = rows.map(row => ({ ...row, active: row.active ? '활성' : '비활성' }));

      const excelManager = new ExcelManager(tableRows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.export(`거래처상품리스트${dayjs().format('YYMMDD')}`);
    });
};

onMounted(() => {
  const { correspondentCode } = dialogRef.value.data;
  currentCorrespondentCode.value = correspondentCode;

  getCorrespondentItems();
});
</script>

<style scoped></style>
