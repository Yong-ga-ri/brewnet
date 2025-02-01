<template>
  <div v-if="refundDetail" class="return-refund-detail">
    <div class="top-area">
      <Button
        v-if="refundDetail.confirmed === OTHER_DEPT_CHECK_STATUS.UNCONFIRMED"
        label="환불 처리 완료로 변경"
        size="small"
        variant="outlined"
        @click="clickRefundConfirmed"
      />
      <Tag
        v-else-if="refundDetail.confirmed === OTHER_DEPT_CHECK_STATUS.CONFIRMED"
        :value="formatKoOtherDeptCheckStatus(refundDetail.confirmed)"
        size="small"
        rounded
        :severity="getReturnStockCheckStatusSeverity(refundDetail.confirmed)"
      />
    </div>
    <div>
      <h3 class="mb-3">반품요청 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>반품요청일자</th>
          <td>{{ refundDetail.returningCreatedAt }}</td>
          <th>반품요청지점</th>
          <td>{{ refundDetail.franchiseName }}</td>
          <th>반품담당자</th>
          <td>{{ refundDetail.returningManager }}</td>
        </tr>
        <tr>
          <th>반품사유</th>
          <td colspan="5" class="align-center">{{ formatKoReturnReason(refundDetail.reason) }}</td>
        </tr>
      </AppTableStyled>
    </div>

    <div>
      <h3 class="mb-3">환불처리 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>처리완료일자</th>
          <td>{{ refundDetail.createdAt }}</td>
          <th>처리상태</th>
          <td>{{ formatKoReturnRefundStatus(refundDetail.status) }}</td>
          <th>환불담당자</th>
          <td>{{ refundDetail.manager }}</td>
        </tr>
        <tr>
          <th>비고사항</th>
          <td colspan="5" class="align-center">{{ refundDetail.comment }}</td>
        </tr>
      </AppTableStyled>
    </div>

    <AppTableStyled>
      <thead>
        <tr>
          <th>상품코드</th>
          <th>상품명</th>
          <th>카테고리</th>
          <th>전체수량</th>
          <th>환불액</th>
          <th>처리상태</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="refundItem in refundDetail.refundHistoryItemList" :key="refundItem.itemUniqueCode">
          <td class="align-center">{{ refundItem.itemUniqueCode }}</td>
          <td class="align-center">{{ refundItem.itemName }}</td>
          <td class="align-center">{{ refundItem.superCategory }} - {{ refundItem.subCategory }}</td>
          <td class="align-center">{{ refundItem.quantity.toLocaleString() }}</td>
          <td class="align-right">{{ refundItem.refundPrice.toLocaleString() }}</td>
          <td class="align-center">{{ refundItem.completed ? '처리완료' : '환불불가' }}</td>
        </tr>
      </tbody>
    </AppTableStyled>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQReturnApi from '@/utils/api/HQReturnApi';
import { OTHER_DEPT_CHECK_STATUS } from '@/utils/constant';
import { formatKoOtherDeptCheckStatus, formatKoReturnReason, formatKoReturnRefundStatus } from '@/utils/format';
import { getReturnStockCheckStatusSeverity } from '@/utils/helper';

const route = useRoute();
const { detailCode } = route.params;
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const refundDetail = ref(null);

const hqReturnApi = new HQReturnApi();

const getPageData = () => {
  hqReturnApi.getReturnRefundDetail(detailCode).then(data => {
    refundDetail.value = data;
  });
};

const confirmRefund = () => {
  hqReturnApi.confirmRefund(detailCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '최종 환불처리 되었습니다.', life: 3000 });
    getPageData();
  });
};

const clickRefundConfirmed = () => {
  showConfirm({
    header: '환불 처리',
    message: '최종 환불 처리 완료로 변경하시겠습니까?',
    acceptLabel: '환불 처리 완료',
    onAccept: confirmRefund,
  });
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.return-refund-detail {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding-top: 32px;

  .top-area {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
}
</style>
