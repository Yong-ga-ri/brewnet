<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppInputText v-model="criteria.itemUniqueCode" label="상품코드 검색" />
      <AppInputText v-model="criteria.itemName" label="상품명 검색" />
    </SearchArea>

    <AppTable
      :columns="columns"
      :rows-per-page="pageSize"
      :paginated-data="paginatedItems"
      :total-elements="totalElements"
      @change-page="onChangePage"
      @reload="onReload"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';

import AppTable from '../common/AppTable.vue';
import AppInputText from '../common/form/AppInputText.vue';
import SearchArea from '../common/SearchArea.vue';

const { correspondentCode, selectedItems } = defineProps({
  correspondentCode: {
    type: [Number, String],
    required: true,
  },
  selectedItems: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['choose', 'remove']);

const page = ref(1);
const paginatedItems = ref([]);
const totalElements = ref(0);
const pageSize = 15;

const getInitialCriteria = () => ({ itemUniqueCode: '', itemName: '' });
const criteria = ref(getInitialCriteria());

const hqCorrespondentApi = new HQCorrespondentApi();

const columns = [
  { field: 'itemUniqueCode', header: '상품코드' },
  { field: 'itemName', header: '상품명' },
  { field: 'purchasePrice', header: '구매단가', render: data => data.purchasePrice.toLocaleString() },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: data => {
            const found = selectedItems.find(e => e.itemCode === data.itemCode);
            if (found) return '취소';
            return '선택';
          },
          clickHandler: data => {
            const found = selectedItems.find(e => e.itemCode === data.itemCode);
            if (found) {
              emit('remove', data);
            } else {
              emit('choose', data);
            }
          },
          getSeverity: data => {
            const found = selectedItems.find(e => e.itemCode === data.itemCode);
            if (found) return 'danger';
            return undefined;
          },
        },
      ],
    },
  },
];

const getCorrespondentItems = () => {
  hqCorrespondentApi
    .getCorrespondentItems({
      page: criteria.value.page,
      pageSize,
      correspondentCode,
      itemUniqueCode: criteria.value.itemUniqueCode,
      itemName: criteria.value.itemName,
    })
    .then(data => {
      paginatedItems.value = data.data;
      totalElements.value = data.totalCount;
    });
};
const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 1;
  getCorrespondentItems();
};

const onSearch = () => {
  getCorrespondentItems();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getCorrespondentItems();
};

const onReload = () => {
  getCorrespondentItems();
};

watch(
  () => correspondentCode,
  newVal => {
    getCorrespondentItems();
  },
  { immediate: true },
);
</script>

<style scoped></style>
