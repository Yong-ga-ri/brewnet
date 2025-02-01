<template>
  <div>
    <h4 class="mb-1">결재이력</h4>
    <AppTableStyled full-width>
      <thead>
        <tr>
          <th>직위</th>
          <th>결재자</th>
          <th>결재상태</th>
          <th>비고사항</th>
          <th>결재일시</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="approvalLine in approvalLines" :key="approvalLine.approverCode">
          <td class="align-center">{{ formatKoEmployeePosition(approvalLine.positionName) }}</td>
          <td class="align-center">{{ approvalLine.approverName }}</td>
          <td class="align-center">
            <template v-if="isNeedMyApproval(approvalLine.approverCode, approvalLine.approved)">
              <Button label="결재 진행" size="small" @click="clickDoApproval" />
            </template>
            <template v-else>
              {{ formatKoApproverApprovedStatus(approvalLine.approved) }}
            </template>
          </td>
          <td class="align-center">{{ approvalLine.comment }}</td>
          <td class="align-center">{{ approvalLine.createdAt }}</td>
        </tr>
      </tbody>
    </AppTableStyled>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { defineAsyncComponent } from 'vue';

import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQExchangeApi from '@/utils/api/HQExchangeApi';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import HQReturnApi from '@/utils/api/HQReturnApi';
import SuperOrderApi from '@/utils/api/SuperOrderApi';
import { APPROVER_APPROVED_STATUS, DRAFT_KIND } from '@/utils/constant';
import { formatKoApproverApprovedStatus, formatKoEmployeePosition } from '@/utils/format';

import AppTableStyled from '../common/AppTableStyled.vue';

const ApprovalModalBody = defineAsyncComponent(() => import('@/components/headQuarter/ApprovalModalBody.vue'));

const { draftKind, draftCode, approvalLines } = defineProps({
  draftKind: {
    type: String,
    required: true,
  },
  draftCode: {
    type: [Number, String],
    required: true,
  },
  approvalLines: {
    type: Array,
    required: true,
  },
  /**
   * approvalLines: [{
   *   approverCode: number,     // 결재자의 멤버코드
   *   positionName: string,     // 결재자의 직위 (enum)
   *   approverName: string,     // 결재자명
   *   approved: string,         // 결재자의 approved 상태
   *   comment: string,          // 결재자의 comment
   *   createdAt: string,        // 결재자의 결재일시
   * }]
   */
});
const emit = defineEmits(['completeApproval']);

const userStore = useUserStore();
const toast = useToast();
const { openModal } = useModal();

const hqPurchaseApi = new HQPurchaseApi();
const superOrderApi = new SuperOrderApi();
const hqReturnApi = new HQReturnApi();
const hqExchangeApi = new HQExchangeApi();

// 내 결재가 필요한 단계인가?
const isNeedMyApproval = (approverCode, approvedStatus) => {
  return approverCode === userStore.memberCode && approvedStatus === APPROVER_APPROVED_STATUS.UNCONFIRMED;
};

const doingApproval = async (approved, comment) => {
  if (!draftKind || !draftCode) {
    toast.add({
      severity: 'error',
      summary: '처리 실패',
      detail: 'draftKind 또는 draftCode가 설정되지 않았습니다.',
      life: 3000,
    });
    return;
  }

  let successMeg = '';

  if (draftKind === DRAFT_KIND.PURCHASE) {
    // 발주에 대한 결재 진행
    if (approved === APPROVER_APPROVED_STATUS.APPROVED) {
      await hqPurchaseApi.approvePurchase({ purchaseCode: draftCode, comment });
      successMeg = '결재 승인되었습니다. 창고시스템으로 주문요청서가 전송되었습니다.';
    } else if (approved === APPROVER_APPROVED_STATUS.REJECTED) {
      await hqPurchaseApi.rejectPurchase({ purchaseCode: draftCode, comment });
    }
  } else if (draftKind === DRAFT_KIND.ORDER) {
    // 주문에 대한 결재 진행
    if (approved === APPROVER_APPROVED_STATUS.APPROVED) {
      await superOrderApi.approveOrder({ orderCode: draftCode, comment });
    } else if (approved === APPROVER_APPROVED_STATUS.REJECTED) {
      await superOrderApi.rejectOrder({ orderCode: draftCode, comment });
    }
  } else if (draftKind === DRAFT_KIND.EXCHANGE) {
    // 교환에 대한 결재 진행
    if (approved === APPROVER_APPROVED_STATUS.APPROVED) {
      await hqExchangeApi.managerApprove({
        exchangeCode: draftCode,
        approval: APPROVER_APPROVED_STATUS.APPROVED,
        comment,
      });
    } else if (approved === APPROVER_APPROVED_STATUS.REJECTED) {
      await hqExchangeApi.managerApprove({
        exchangeCode: draftCode,
        approval: APPROVER_APPROVED_STATUS.REJECTED,
        comment,
      });
    }
  } else if (draftKind === DRAFT_KIND.RETURN) {
    // 반품에 대한 결재 진행
    await hqReturnApi.approval({ returnCode: draftCode, approved, comment });
    if (approved === APPROVER_APPROVED_STATUS.APPROVED) {
      successMeg = '결재 승인되었습니다. 창고시스템으로 반품요청서가 전송되었습니다.';
    }
  }

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMeg || '결재가 저장되었습니다.', life: 3000 });

  // 데이터 새로고침
  emit('completeApproval');
};

const clickDoApproval = () => {
  openModal({
    component: ApprovalModalBody,
    header: '결재 진행',
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      const { approved, comment } = callbackParams;
      doingApproval(approved, comment);
    },
  });
};
</script>

<style scoped></style>
