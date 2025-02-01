<template>
  <AppTableStyled full-width>
    <tbody>
      <tr>
        <th>교환요청일자</th>
        <td>{{ exchangeOtherDetail.exchangeCreatedAt }}</td>
        <th>교환요청지점</th>
        <td>{{ exchangeOtherDetail.franchiseName }}</td>
        <th>교환사유</th>
        <td>{{ formatKoExchangeReason(exchangeOtherDetail.reason) }}</td>
        <th>교환담당자</th>
        <td>{{ exchangeOtherDetail.exchangeManager }}</td>
      </tr>
      <tr></tr>
      <tr>
        <th>처리완료일자</th>
        <td>{{ exchangeOtherDetail.createdAt }}</td>
        <th>비고사항</th>
        <td>{{ exchangeOtherDetail.comment }}</td>
        <th>처리상태</th>
        <td>{{ formatKoExchangeOtherStatus(exchangeOtherDetail.status) }}</td>
        <th>처리담당자</th>
        <td>{{ exchangeOtherDetail.manager }}</td>
      </tr>

      <tr>
        <th>상품코드</th>
        <th colspan="3">상품명</th>
        <th colspan="2">카테고리</th>
        <th colspan="1">재입고수량</th>
        <th colspan="1">전체수량</th>
      </tr>
      <tr v-for="item in exchangeOtherDetail.exchangeHistoryItemList" :key="item.itemUniqueCode">
        <td class="align-center">{{ item.itemUniqueCode }}</td>
        <td colspan="3">{{ item.itemName }}</td>
        <td colspan="2">{{ item.superCategory + ' > ' + item.subCategory }}</td>
        <td colspan="1" class="align-right">{{ item.restockQuantity.toLocaleString() }}</td>
        <td colspan="1" class="align-right">{{ item.quantity.toLocaleString() }}</td>
      </tr>
    </tbody>
  </AppTableStyled>
</template>

<script setup>
import { formatKoExchangeReason, formatKoExchangeOtherStatus } from '@/utils/format';

import AppTableStyled from '../common/AppTableStyled.vue';

const { exchangeOtherDetail } = defineProps({
  exchangeOtherDetail: {
    type: Object,
    required: true,
  },
});
</script>

<style scoped></style>
