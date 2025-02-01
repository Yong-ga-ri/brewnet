<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
      <AppInputText v-model="criteria.itemUniqueCode" label="상품코드 검색" />
      <AppInputText v-model="criteria.name" label="상품명 검색" />
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
import { onMounted, ref, watch } from 'vue';

import ItemApi from '@/utils/api/ItemApi';

import AppTable from '../common/AppTable.vue';
import AppInputText from '../common/form/AppInputText.vue';
import SearchArea from '../common/SearchArea.vue';

const { selectedItems } = defineProps({
  selectedItems: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['choose', 'remove']);

const page = ref(0);
const paginatedItems = ref([]);
const totalElements = ref(0);
const pageSize = 15;

const getInitialCriteria = () => ({ itemUniqueCode: '', name: '' });
const criteria = ref(getInitialCriteria());

const itemApi = new ItemApi();

const columns = [
  { field: 'itemUniqueCode', header: '상품코드' },
  { field: 'name', header: '상품명' },
  { field: 'categoryName', header: '카테고리' },
  { field: 'sellingPrice', header: '단가', render: data => data.sellingPrice.toLocaleString() },
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

const getItems = () => {
  itemApi
    .getItems({
      page: page.value,
      pageSize,
      itemUniqueCode: criteria.value.itemUniqueCode,
      itemName: criteria.value.name,
    })
    .then(data => {
      paginatedItems.value = data.content;
      totalElements.value = data.totalElements;
    });
};
const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 0;
  getItems();
};

const onSearch = () => {
  getItems();
};

const onChangePage = event => {
  page.value = event.page;
  getItems();
};

const onReload = () => {
  getItems();
};

onMounted(() => {
  getItems();
});

// watch(
//   newVal => {
//     getItems();
//   },
//   { immediate: true },
// );
</script>

<style scoped></style>
