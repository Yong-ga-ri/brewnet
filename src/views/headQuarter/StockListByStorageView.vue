<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppSelect
        v-model="selectedStorage"
        :options="storageOptions"
        label="창고명"
        label-position="left"
        full-width
        :initial-value="selectedStorage"
      />
      <AppInputText v-model="criteria.keyword" placeholder="상품명으로 검색" class="criteria keyword" />
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

    <DynamicDialog />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useModal } from '@/hooks/useModal';
import HQStorageApi from '@/utils/api/HQStorageApi';
import { SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { makeSelectOption } from '@/utils/helper';

const toast = useToast();
const { openModal } = useModal();

const page = ref(1);
const pageSize = ref(15);
const paginatedItems = ref([]);
const totalElements = ref(0);

const hqStorageApi = new HQStorageApi();

const selectedStorage = ref('');
const storageOptions = ref([]);

const getInitialCriteria = () => ({
  criteria: SEARCH_CRITERIA.ITEM_NAME,
  keyword: '',
});
const criteria = ref(getInitialCriteria());

const ChangeStockModalBody = defineAsyncComponent(() => import('@/components/headQuarter/ChangeStockModalBody.vue'));

function changeStock(data) {
  openModal({
    component: ChangeStockModalBody,
    header: '재고에 합산할 수량을 입력해 주세요.',
    data: {
      storageCode: data.storageCode,
      itemCode: data.itemCode,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      if (callbackParams.reload) {
        getStockList();
      }
    },
  });
}

const getCellHighlightColor = data => {
  if (data.availableStock <= data.safetyStock) return 'danger';
  return undefined;
};

const columns = [
  {
    field: 'itemUniqueCode',
    header: '상품코드',
    sortable: true,
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'itemName',
    header: '상품명',
    sortable: true,
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'availableStock',
    header: '가용재고',
    render: data => data.availableStock.toLocaleString(),
    alignment: 'right',
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'inStock',
    header: '입고예정재고',
    render: data => data.inStock.toLocaleString(),
    alignment: 'right',
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'safetyStock',
    header: '안전재고값',
    render: data => data.safetyStock.toLocaleString(),
    alignment: 'right',
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'outStock',
    header: '출고예정재고',
    render: data => data.outStock.toLocaleString(),
    alignment: 'right',
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: 'currentStock',
    header: '현재재고',
    render: data => data.currentStock.toLocaleString(),
    alignment: 'right',
    getHighlightColor: getCellHighlightColor,
  },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '재고조정',
          clickHandler: changeStock,
        },
      ],
    },
  },
];

const getStorageList = () => {
  hqStorageApi.getStoraeNames().then(data => {
    // 셀렉트 박스 옵션 목록 셋팅
    storageOptions.value = data.map(storage => makeSelectOption(storage.storageName, storage.storageCode.toString()));

    if (storageOptions.value.length > 0) {
      selectedStorage.value = storageOptions.value[0].value;
      getStockList();
    }
  });
};

const getStockList = () => {
  hqStorageApi
    .getStock({
      page: page.value,
      pageSize: pageSize.value,
      storageCode: selectedStorage.value,
      itemName: criteria.value.keyword,
    })
    .then(data => {
      paginatedItems.value = data.data;
      totalElements.value = data.totalCount;
    });
};

const onSearch = () => {
  getStockList();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  getStockList();
};

const onReload = () => {
  getStockList();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getStockList();
};

const onExportExcel = () => {
  if (!selectedStorage.value) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: '창고가 선택되어야 합니다.', life: 3000 });
    return;
  }

  hqStorageApi
    .getAllStockList({
      storageCode: selectedStorage.value,
      itemName: criteria.value.keyword,
    })
    .then(rows => {
      const orderedFields = columns.filter(e => e.field).map(e => e.field);
      const headerNames = columns.filter(e => e.field).map(e => e.header);

      const excelManager = new ExcelManager(rows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.export(`창고별재고리스트${dayjs().format('YYMMDD')}`);
    });
};

onMounted(() => {
  getStorageList();
});
</script>

<style scoped></style>
