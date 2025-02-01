<template>
  <AppTableStyled>
    <tbody>
      <tr>
        <th>교환요청지점</th>
        <td>{{ exchangeDetail.franchiseName }}</td>
        <th>교환요청일</th>
        <td colspan="2">{{ exchangeDetail.createdAt }}</td>
        <th>교환담당자</th>
        <td>{{ exchangeDetail.memberName }}</td>
      </tr>
      <tr>
        <th>교환사유</th>
        <td colspan="6" class="align-center">{{ formatKoExchangeReason(exchangeDetail.reason) }}</td>
      </tr>
      <tr>
        <th>교환 상세사유</th>
        <td colspan="6" class="align-center">{{ exchangeDetail.explanation }}</td>
      </tr>
      <tr>
        <th :rowspan="exchangeDetail.exchangeItemList.length + 1">교환상품</th>
        <th>상품코드</th>
        <th colspan="2">상품명</th>
        <th>카테고리</th>
        <th>수량</th>
        <th>부분 합계</th>
      </tr>
      <tr v-for="exchangeItem in exchangeDetail.exchangeItemList" :key="exchangeItem.itemCode">
        <td class="align-center">{{ exchangeItem.itemUniqueCode }}</td>
        <td class="align-center" colspan="2">{{ exchangeItem.itemName }}</td>
        <td class="align-center">{{ exchangeItem.superCategory }} - {{ exchangeItem.subCategory }}</td>
        <td class="align-center">{{ exchangeItem.quantity.toLocaleString() }}</td>
        <td class="align-right">{{ cancelInvoice ? '-' : '' }}{{ exchangeItem.partSumPrice.toLocaleString() }}</td>
      </tr>
    </tbody>
  </AppTableStyled>
</template>

<script setup>
import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { formatKoExchangeReason } from '@/utils/format';

const { exchangeDetail, cancelInvoice } = defineProps({
  exchangeDetail: {
    type: Object,
    required: true,
  },
  cancelInvoice: {
    type: Boolean,
    required: false,
    default: false,
  },
});
</script>

<style scoped></style>
