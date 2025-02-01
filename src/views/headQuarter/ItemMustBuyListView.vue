<template>
  <div>
    <AppTable
      :columns="columns"
      :total-elements="totalElements"
      :paginated-data="mustBuyItems"
      :rows-per-page="pageSize"
      :add-button="{
        label: '필수구매상품 등록',
        clickHandler: clickAddMustBuy,
      }"
      @reload="onReload"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';

import AppTable from '@/components/common/AppTable.vue';
import { useModal } from '@/hooks/useModal';
import HQItemMustBuyApi from '@/utils/api/HQItemMustBuyApi';

const AddMustBuyItemModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/AddMustBuyItemModalBody.vue'),
);

const { openModal } = useModal();

const mustBuyItems = ref([]);
const pageSize = ref(15);
const totalElements = computed(() => {
  return mustBuyItems.value.length;
});

const hqItemMustBuyApi = new HQItemMustBuyApi();

const columns = [
  {
    field: 'mandatoryPurchaseCode',
    header: '구분코드',
  },
  { field: 'itemCode', header: '상품코드' },
  { field: 'itemName', header: '상품명' },
  { field: 'quantity', header: '최소구매수량', render: data => data.quantity.toLocaleString() },
  {
    field: 'dueDate',
    header: '마감 기한',
    render: data => {
      const now = dayjs().format('YYYY-MM-DD');
      const formattedDueDate = dayjs(data.dueDate).format('YYYY-MM-DD');
      const diffDay = dayjs(formattedDueDate).diff(dayjs(now), 'day');
      const signValue = Math.sign(diffDay);
      const sign = signValue === -1 ? '+' : '-'; // 이미 지난거는 +로 표시

      return `${formattedDueDate} (D${sign}${Math.abs(diffDay)})`;
    },
  },
  { field: 'createdAt', header: '생성일시' },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '수정',
          clickHandler: data => {
            openModal({
              component: AddMustBuyItemModalBody,
              header: '필수구매상품 정보 수정',
              data: {
                itemData: data,
              },
              onClose: opt => {
                const callbackParams = opt.data;
                if (!callbackParams) return;

                if (callbackParams.reload) {
                  onReload();
                }
              },
            });
          },
        },
      ],
    },
  },
];

const getMustBuyItems = () => {
  hqItemMustBuyApi.getMustBuyItems().then(data => {
    mustBuyItems.value = data;
  });
};

const onReload = () => {
  getMustBuyItems();
};

const clickAddMustBuy = () => {
  openModal({
    component: AddMustBuyItemModalBody,
    header: '필수구매상품 지정',
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      if (callbackParams.reload) {
        onReload();
      }
    },
  });
};

onMounted(() => {
  getMustBuyItems();
});
</script>

<style scoped></style>
