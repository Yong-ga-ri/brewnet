<template>
  <div>
    <SearchArea grid @form-reset="onReset" @search="onSearch">
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
      @reload="onReload"
      @change-page="onChangePage"
    />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQItemApi from '@/utils/api/HQItemApi';
import { CRITERIA_ITEM_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import { formatKoSearchCriteria } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

const toast = useToast();
const { showConfirm } = useAppConfirmModal();
const router = useRouter();

const page = ref(0);
const pageSize = ref(15);
const paginatedItems = ref([]);
const totalElements = ref(0);

const getInitialCriteria = () => ({
  criteria: SEARCH_CRITERIA.ITEM_NAME,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const criteriaOptions = computed(() => {
  return CRITERIA_ITEM_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const hqItemApi = new HQItemApi();

const getItems = () => {
  hqItemApi
    .getItems({
      page: page.value,
      pageSize: pageSize.value,
      itemName: criteria.value.criteria === SEARCH_CRITERIA.ITEM_NAME ? criteria.value.keyword : undefined,
      itemUniqueCode: criteria.value.criteria === SEARCH_CRITERIA.ITEM_UNIQUE_CODE ? criteria.value.keyword : undefined,
    })
    .then(data => {
      paginatedItems.value = data.content;
      totalElements.value = data.totalElements;
    })
    .catch(err => {
      paginatedItems.value = [];
      totalElements.value = 0;
    });
};

const onReload = () => {
  getItems();
};

const clickEdit = targetItem => {
  router.push({ name: 'hq:stock:item:edit', params: { itemCode: targetItem.itemCode } });
};

const onChangeActiveStatus = async targetItem => {
  let successMsg = '';

  if (targetItem.active) {
    // 활성 -> 비활성

    await hqItemApi.deactivateItem(targetItem.itemCode);
    successMsg = '상품이 비활성화 되었습니다.';
  } else {
    // 비활성 -> 활성

    await hqItemApi.editItem({
      itemCode: targetItem.itemCode,
      categoryCode: targetItem.categoryCode,
      name: targetItem.name,
      purchasePrice: targetItem.purchasePrice,
      sellingPrice: targetItem.sellingPrice,
      imageUrl: targetItem.imageUrl,
      safetyStock: targetItem.safetyStock,
      itemUniqueCode: targetItem.itemUniqueCode,
      correspondentCode: targetItem.correspondentCode,
      active: true,
    });
    successMsg = '상품이 활성화 되었습니다.';
  }

  if (!successMsg) return;

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  onReload();
};

const clickChangeStatus = targetItem => {
  showConfirm({
    header: '활성 상태 변경',
    message: `상품코드 ${targetItem.itemUniqueCode} [${targetItem.name}]의 상태를 ${targetItem.active ? '비활성화' : '활성화'}합니다.`,
    acceptLabel: targetItem.active ? '비활성 상태로 변경' : '활성 상태로 변경',
    onAccept: () => onChangeActiveStatus(targetItem),
    danger: targetItem.active,
  });
};

const columns = [
  { field: 'categoryName', header: '카테고리' },
  { field: 'itemUniqueCode', header: '상품코드' },
  { field: 'name', header: '상품명' },
  { field: 'sellingPrice', header: '판매단가', render: data => data.sellingPrice.toLocaleString(), alignment: 'right' },
  {
    field: 'purchasePrice',
    header: '구매단가',
    render: data => data.purchasePrice.toLocaleString(),
    alignment: 'right',
  },
  { field: 'correspondentName', header: '취급거래처' },
  { field: 'safetyStock', header: '안전재고값', render: data => data.safetyStock.toLocaleString() },
  {
    field: 'imageUrl',
    header: '상품이미지',
    template: {
      image: {
        getSrc: data => data.imageUrl || undefined,
      },
    },
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
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '수정',
          clickHandler: data => clickEdit(data),
        },
        {
          getLabel: data => (data.active ? '비활성' : '활성'),
          clickHandler: data => clickChangeStatus(data),
          getSeverity: data => (data.active ? 'danger' : undefined),
        },
      ],
    },
  },
];

const onSearch = () => {
  getItems();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 0;
  getItems();
};

const onChangePage = event => {
  page.value = event.page;
  getItems();
};

onMounted(() => {
  getItems();
});
</script>

<style scoped></style>
