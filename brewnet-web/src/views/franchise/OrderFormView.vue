<template>
  <div class="order-form-container">
    <div class="top-area">
      <div class="must-buy" :style="{ visibility: mustBuyItems.length > 0 ? 'visible' : 'hidden' }">
        <Message severity="warn" icon="pi pi-megaphone" size="small">필수구매상품 안내</Message>
        <Message
          v-for="mustBuyItem in mustBuyItems"
          :key="mustBuyItem.itemCode"
          severity="warn"
          variant="outlined"
          size="small"
          >{{ mustBuyItem.itemName }} - {{ mustBuyItem.quantity.toLocaleString() }}개</Message
        >
      </div>

      <Button label="주문하기" class="save-button" @click="clickSave" />
    </div>

    <div class="table-area">
      <SelectItemTable :selected-items="selectedItems" @choose="chooseItem" @remove="removeItem" />
      <AppTableStyled>
        <thead>
          <tr>
            <th v-for="header in tableHeader" :key="header">{{ header }}</th>
          </tr>
        </thead>
        <tbody>
          <template v-if="selectedItems.length > 0">
            <tr v-for="item in selectedItems" :key="item.itemCode">
              <td class="align-center">
                <Button icon="pi pi-trash" variant="text" rounded severity="secondary" @click="removeItem(item)" />
              </td>
              <td class="align-center">{{ item.itemUniqueCode }}</td>
              <td>{{ item.name }}</td>
              <td>
                <AppInputText v-model="item.quantity" number-mode text-align="right" />
              </td>
              <td class="align-right">{{ item.sellingPrice.toLocaleString() }}</td>
              <td class="align-right">{{ calculateSum(item.sellingPrice, item.quantity).toLocaleString() }}</td>
              <td class="align-right">
                {{ calculateTax(calculateSum(item.sellingPrice, item.quantity)).toLocaleString() }}
              </td>
            </tr>
          </template>
          <template v-else>
            <tr>
              <td colspan="7" class="empty-td">주문할 상품을 선택해주세요.</td>
            </tr>
          </template>
        </tbody>
        <tfoot>
          <tr>
            <td>총 합계</td>
            <td colspan="4" class="align-center">{{ total.toLocaleString() }}</td>
            <td class="align-right">{{ totalSupplyValue.toLocaleString() }}</td>
            <td class="align-right">{{ totalTaxValue.toLocaleString() }}</td>
          </tr>
        </tfoot>
      </AppTableStyled>
    </div>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import EmptyContent from '@/components/common/EmptyContent.vue';
import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import SelectItemTable from '@/components/franchise/SelectItemTable.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useUserStore } from '@/stores/user';
import FCItemApi from '@/utils/api/FCItemApi';
import FCOrderApi from '@/utils/api/FCOrderApi';

const userStore = useUserStore();
const toast = useToast();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();

const tableHeader = ref(['', '상품코드', '상품명', '수량', '단가', '공급가액', '부가세']);
const selectedItems = ref([]); // 선택된 상품(코드)들
const totalSupplyValue = ref(0);
const totalTaxValue = ref(0);
const total = computed(() => totalSupplyValue.value + totalTaxValue.value);
const mustBuyItems = ref([]);

const fcOrderApi = new FCOrderApi();
const fcItemApi = new FCItemApi();

const calculateSum = (price, quantity) => {
  if (!price || !quantity) return 0;
  return price * quantity;
};

const calculateTax = sum => {
  return sum * 0.1;
};

const chooseItem = data => {
  selectedItems.value = selectedItems.value.concat([{ ...data, quantity: null }]);
};

const removeItem = data => {
  selectedItems.value = selectedItems.value.filter(e => e.itemCode !== data.itemCode);
};

const checkForm = () => {
  try {
    if (selectedItems.value.length === 0) throw new Error('주문 상품을 입력해주세요.');
    if (totalSupplyValue.value === 0) throw new Error('수량을 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const onOrderSave = () => {
  fcOrderApi
    .createOrder({
      items: selectedItems.value.map(e => ({ itemCode: e.itemCode, quantity: e.quantity })),
    })
    .then(result => {
      toast.add({
        severity: 'success',
        summary: '처리 성공',
        detail: '주문이 등록되었습니다.',
        life: 3000,
      });

      // 상세 페이지로 이동
      router.replace({ name: 'fc:home:order:detail', params: { orderCode: result.orderCode } });
    });
};

const clickSave = () => {
  const isPass = checkForm();
  if (!isPass) return;
  showConfirm({
    header: '최종 확인',
    message: '입력된 내용으로 주문 등록이 진행됩니다.',
    acceptLabel: '주문 등록',
    onAccept: onOrderSave,
  });
};

// items가 변하면(items 안의 quantity가 변하면) 총합 계산
watch(
  selectedItems,
  newItems => {
    // 공급가액 총액 계산
    totalSupplyValue.value = newItems
      .map(e => calculateSum(e.sellingPrice, e.quantity))
      .reduce((prev, current) => prev + current, 0);

    // 부가세 총액 계산
    totalTaxValue.value = newItems
      .map(e => calculateTax(calculateSum(e.sellingPrice, e.quantity)))
      .reduce((prev, current) => prev + current, 0);
  },
  { deep: true }, // items 내용이 변하면 watch 되도록 설정
);

onMounted(() => {
  fcItemApi.getMustBuyItems().then(data => {
    mustBuyItems.value = data;
  });
});
</script>

<style scoped>
.order-form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .top-area {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .must-buy {
    display: flex;
    gap: 10px;
  }

  .table-area {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: flex-start;

    .empty-td {
      text-align: center;
    }
  }

  .save-button {
    align-self: flex-end;
  }
}
</style>
