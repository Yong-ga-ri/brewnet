<template>
  <div class="order-detail-container">
    <template v-if="orderDetail">
      <div class="top-area">
        <Tag
          rounded
          :value="formatKoOrderStatus(orderDetail.orderStatus)"
          :severity="getOrderStatusSeverity(orderDetail.orderStatus)"
          class="mb-1"
        />
        <div class="top-buttons">
          <Button
            v-if="isShowRequestApproval"
            label="결재요청"
            variant="outlined"
            size="small"
            @click="clickRequestApproval"
          />
          <Button
            v-if="isShowRequestApproval"
            label="주문요청 반려"
            variant="outlined"
            severity="danger"
            size="small"
            @click="clickRejectDraft"
          />
          <Button label="주문요청서 출력" variant="outlined" size="small" @click="clickPrintOrder" />
          <Button
            label="거래명세서 출력"
            variant="outlined"
            size="small"
            :disabled="!enableInvoice"
            @click="clickPrintInvoice"
          />

          <!-- 기안 담당자인 경우에만 결재요청취소 버튼 표시 -->
          <Button
            v-if="isShowRequestApprovalCancel"
            label="결재요청취소"
            severity="danger"
            size="small"
            variant="outlined"
            @click="clickCancel"
          />
        </div>
      </div>

      <div class="body-area">
        <h1>주문상세</h1>

        <DraftApprovalLine
          class="approval-line-table"
          :draft-manager-name="orderDetail.managerName"
          :approval-lines="orderApprovalLines"
        />

        <OrderDetailTable v-if="orderDetail" :order-detail="orderDetail" />
      </div>

      <DraftApprovalHistoryTable
        :approval-lines="orderApprovalLines"
        :draft-kind="DRAFT_KIND.ORDER"
        :draft-code="orderCode"
        @complete-approval="onCompleteApproval"
      />

      <DynamicDialog />

      <PrintOrderPdfPreviewModal v-model:show="showPrintPdf" :order-detail="orderDetail" :print-type="printType" />
    </template>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import DraftApprovalHistoryTable from '@/components/headQuarter/DraftApprovalHistoryTable.vue';
import DraftApprovalLine from '@/components/headQuarter/DraftApprovalLine.vue';
import OrderDetailTable from '@/components/headQuarter/OrderDetailTable.vue';
import PrintOrderPdfPreviewModal from '@/components/headQuarter/PrintOrderPdfPreviewModal.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQOrderApi from '@/utils/api/HQOrderApi';
import { DRAFT_KIND, ORDER_STATUS, ORDER_STEP_LIST, PRINT_TYPE } from '@/utils/constant';
import { formatKoOrderStatus } from '@/utils/format';
import { getOrderStatusSeverity } from '@/utils/helper';

const ApprovalRequestModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/ApprovalRequestModalBody.vue'),
);
const DraftRejectModalBody = defineAsyncComponent(() => import('@/components/headQuarter/DraftRejectModalBody.vue'));

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();
const { openModal } = useModal();

const orderDetail = ref(null);
const orderApprovalLines = ref([]);
const isShowRequestApprovalCancel = computed(() => {
  // 기안자이고
  // PENDING 상태이고
  // 결재라인이 1개 이상일 때
  return (
    orderDetail.value.managerName === userStore.username &&
    orderDetail.value.orderStatus === ORDER_STATUS.PENDING &&
    orderApprovalLines.value.length > 0
  );
});

const isCompleted = computed(() => {
  return orderDetail.value.orderStatus === ORDER_STATUS.SHIPPED;
});

const isShowRequestApproval = computed(() => {
  // 신규요청 상태이거나
  // 결재라인이 아무것도 없을 때 (결재취소 상태일 때)
  return (
    orderDetail.value.orderStatus === ORDER_STATUS.REQUESTED ||
    (orderDetail.value.orderStatus === ORDER_STATUS.PENDING && orderApprovalLines.value.length === 0)
  );
});
const isBeforeApproved = computed(() => {
  const approveStepIndex = ORDER_STEP_LIST.indexOf(ORDER_STATUS.APPROVED);
  const currentStepIndex = ORDER_STEP_LIST.indexOf(orderDetail.value.orderStatus);

  if (currentStepIndex === -1) return null; // 정상 step 목록에 없으면 false

  return currentStepIndex < approveStepIndex;
});
const enableInvoice = computed(() => {
  if (isBeforeApproved.value === null) return false;
  return !isBeforeApproved.value;
});

const hqOrderApi = new HQOrderApi();
const { orderCode } = route.params;

const showPrintPdf = ref(false);
const printType = ref(PRINT_TYPE.HQ.ORDER_DRAFT);

const getOrderDetailPageData = () => {
  hqOrderApi.getOrderDetail(orderCode).then(data => {
    orderDetail.value = data;
  });

  hqOrderApi.getOrderApprovalLines(orderCode).then(data => {
    orderApprovalLines.value = data.map(e => ({ ...e, positionName: e.position, approverCode: e.approverMemberCode }));
  });
};

const handleRequestApproval = (approverCode, comment) => {
  hqOrderApi.requestApproval({ orderCode, superManagerMemberCode: approverCode, comment }).then(() => {
    toast.add({
      severity: 'success',
      summary: '처리 성공',
      detail: '주문에 대한 결재요청이 등록되었습니다.',
      life: 3000,
    });

    // page reload
    getOrderDetailPageData();
  });
};

const clickRequestApproval = () => {
  openModal({
    component: ApprovalRequestModalBody,
    header: '결재요청',
    data: {
      draftKind: DRAFT_KIND.ORDER,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      const { approverCode, comment } = callbackParams;

      handleRequestApproval(approverCode, comment);
    },
  });
};

const clickPrintOrder = () => {
  printType.value = PRINT_TYPE.HQ.ORDER_DRAFT;
  showPrintPdf.value = true;
};

const clickPrintInvoice = () => {
  printType.value = PRINT_TYPE.HQ.ORDER_INVOICE;
  showPrintPdf.value = true;
};

const cancelOrder = () => {
  hqOrderApi.cancelApproval(orderCode).then(() => {
    toast.add({ severity: 'error', summary: '처리 성공', detail: '결재 요청이 취소되었습니다.', life: 3000 });

    // page reload
    getOrderDetailPageData();
  });
};

const clickCancel = () => {
  showConfirm({
    header: '결재 요청 취소',
    message: '결재 요청을 취소하시겠습니까?',
    acceptLabel: '결재 요청 취소',
    onAccept: cancelOrder,
    danger: true,
  });
};

const onCompleteApproval = () => {
  getOrderDetailPageData();
};

// 기안자가 주문요청 반려
const handleRejectDraft = comment => {
  hqOrderApi.rejectDraft({ orderCode, comment }).then(() => {
    toast.add({
      severity: 'success',
      summary: '처리 성공',
      detail: '주문요청을 반려했습니다.',
      life: 3000,
    });

    // page reload
    getOrderDetailPageData();
  });
};
const clickRejectDraft = () => {
  openModal({
    component: DraftRejectModalBody,
    header: '주문요청 반려',
    data: {
      draftKind: DRAFT_KIND.ORDER,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      const { comment } = callbackParams;

      handleRejectDraft(comment);
    },
  });
};

onMounted(() => {
  // 주문 상세 데이터 셋팅
  getOrderDetailPageData();
});
</script>

<style scoped>
.order-detail-container {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .top-area {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .top-buttons {
      display: flex;
      gap: 5px;
    }
  }

  .body-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;

    .approval-line-table {
      align-self: flex-end;
    }
  }
}
</style>
