<template>
  <AppTableStyled>
    <tbody>
      <tr>
        <th>반품요청지점</th>
        <td>{{ returnDetail.franchiseName }}</td>
        <th>반품요청일</th>
        <td colspan="2">{{ returnDetail.createdAt }}</td>
        <th>반품담당자</th>
        <td>{{ returnDetail.memberName }}</td>
      </tr>
      <tr>
        <th>반품사유</th>
        <td colspan="6" class="align-center">{{ formatKoReturnReason(returnDetail.reason) }}</td>
      </tr>
      <tr>
        <th>반품 상세사유</th>
        <td colspan="6" class="align-center">{{ returnDetail.explanation }}</td>
      </tr>
      <tr>
        <th :rowspan="returnDetail.returningItemList.length + 1">반품상품</th>
        <th>상품코드</th>
        <th colspan="2">상품명</th>
        <th>카테고리</th>
        <th>수량</th>
        <th>부분 합계</th>
      </tr>
      <tr v-for="returnItem in returnDetail.returningItemList" :key="returnItem.itemCode">
        <td class="align-center">{{ returnItem.itemUniqueCode }}</td>
        <td class="align-center" colspan="2">{{ returnItem.itemName }}</td>
        <td class="align-center">{{ returnItem.superCategory }} - {{ returnItem.subCategory }}</td>
        <td class="align-center">{{ returnItem.quantity.toLocaleString() }}</td>
        <td class="align-right">{{ cancelInvoice ? '-' : '' }}{{ returnItem.partSumPrice.toLocaleString() }}</td>
      </tr>
    </tbody>
  </AppTableStyled>
</template>

<script setup>
import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { formatKoReturnReason } from '@/utils/format';

const { returnDetail, cancelInvoice } = defineProps({
  returnDetail: {
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
