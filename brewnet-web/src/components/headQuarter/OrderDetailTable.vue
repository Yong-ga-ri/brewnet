<template>
  <AppTableStyled full-width>
    <tbody>
      <tr>
        <th>주문일자</th>
        <td>{{ orderDetail.createdAt }}</td>
        <th>주문지점</th>
        <td>{{ orderDetail.franchiseName }}</td>
        <th>주문금액</th>
        <td>{{ orderDetail.sumPrice.toLocaleString() }}</td>
        <th>주문담당자</th>
        <td>{{ orderDetail.managerName }}</td>
      </tr>
      <tr>
        <th>비고사항</th>
        <td colspan="7">{{ orderDetail.comment }}</td>
      </tr>
      <tr>
        <th>상품코드</th>
        <th colspan="3">상품명</th>
        <th>수량</th>
        <th>단가</th>
        <th>공급가액</th>
        <th>부가세</th>
      </tr>
      <tr v-for="item in orderDetail.orderItemList" :key="item.itemCode">
        <td class="align-center">{{ item.itemCode }}</td>
        <td colspan="3">{{ item.name }}</td>
        <td class="align-right">{{ item.quantity.toLocaleString() }}</td>
        <td class="align-right">{{ (item.partSum / item.quantity).toLocaleString() }}</td>
        <td class="align-right">{{ item.partSum.toLocaleString() }}</td>
        <td class="align-right">{{ (item.partSum * 0.1).toLocaleString() }}</td>
      </tr>
      <tr>
        <th>총 주문금액</th>
        <td class="align-right" colspan="7">{{ totalPrice.toLocaleString() }}</td>
      </tr>
    </tbody>
  </AppTableStyled>
</template>

<script setup>
import { computed } from 'vue';

import AppTableStyled from '../common/AppTableStyled.vue';

const { orderDetail } = defineProps({
  orderDetail: {
    type: Object,
    required: true,
  },
});

const totalPrice = computed(() => {
  if (orderDetail && orderDetail.orderItemList) {
    const totalPartSum = orderDetail.orderItemList.reduce((sum, item) => {
      return sum + item.partSum;
    }, 0);
    return (totalPartSum * 1.1).toLocaleString();
  }
  return '0';
});
</script>

<style scoped></style>
