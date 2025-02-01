<template>
  <div v-if="exchangeDetail" class="exchange-detail-container">
    <div class="top-area">
      <Tag
        :value="formatKoExchangeStatus(exchangeDetail.status)"
        :severity="getExchangeStatusSeverity(exchangeDetail.status)"
        class="status"
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
          label="교환요청 반려"
          variant="outlined"
          severity="danger"
          size="small"
          @click="clickRejectDraft"
        />
        <Button
          label="교환요청서 출력"
          variant="outlined"
          size="small"
          :disabled="!isShowPrintDraft"
          @click="clickPrintDraft"
        />

        <Button
          v-if="isShowCancelRequestApproval"
          label="결재요청취소"
          severity="danger"
          size="small"
          variant="outlined"
          @click="clickCancelRequestApproval"
        />
      </div>
    </div>

    <h1 class="title">교환 요청서</h1>

    <DraftApprovalLine
      class="approval-line-table"
      :draft-manager-name="exchangeDetail.memberName"
      :approval-lines="approverList"
    />

    <ExchangeDetailTable :exchange-detail="exchangeDetail" />

    <div v-if="exchangeDetail.exchangeImageList.length > 0">
      <AppLabel use-margin-bottom label="교환 상품 사진" />
      <AppImageList :images="exchangeDetail.exchangeImageList" />
    </div>

    <AppLabelText
      v-if="exchangeDetail.memberName"
      label="기안자 비고사항"
      :text="exchangeDetail.comment || '(비어있음)'"
    />

    <DraftApprovalHistoryTable
      :approval-lines="approverList"
      :draft-kind="DRAFT_KIND.EXCHANGE"
      :draft-code="exchangeCode"
      @complete-approval="onCompleteApproval"
    />

    <DynamicDialog />
    <PrintExchangePdfPreviewModal
      v-model:show="showPrintPdf"
      :exchange-detail="exchangeDetail"
      :print-type="printType"
    />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppImageList from '@/components/common/AppImageList.vue';
import AppLabel from '@/components/common/AppLabel.vue';
import AppLabelText from '@/components/common/AppLabelText.vue';
import DraftApprovalHistoryTable from '@/components/headQuarter/DraftApprovalHistoryTable.vue';
import DraftApprovalLine from '@/components/headQuarter/DraftApprovalLine.vue';
import ExchangeDetailTable from '@/components/headQuarter/ExchangeDetailTable.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQExchangeApi from '@/utils/api/HQExchangeApi';
import { DRAFT_KIND, DRAFTER_APPROVED, PRINT_TYPE, EXCHANGE_STATUS, EXCHANGE_STEP_LIST } from '@/utils/constant';
import { formatKoExchangeStatus } from '@/utils/format';
import { getExchangeStatusSeverity } from '@/utils/helper';

// import PrintExchangePdfPreviewModal from '@/components/headQuarter/PrintExchangePdfPreviewModal.vue';

const ApprovalRequestModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/ApprovalRequestModalBody.vue'),
);
const DraftRejectModalBody = defineAsyncComponent(() => import('@/components/headQuarter/DraftRejectModalBody.vue'));

const route = useRoute();
const { exchangeCode } = route.params;
const router = useRouter();
const { openModal } = useModal();
const toast = useToast();
const { showConfirm } = useAppConfirmModal();
const userStore = useUserStore();

const exchangeDetail = ref(null);
const approverList = ref([]);
const showPrintPdf = ref(false);
const printType = ref(PRINT_TYPE.HQ.EXCHANGE_DRAFT);

const isBeforeApproved = computed(() => {
  const approveStepIndex = EXCHANGE_STEP_LIST.indexOf(EXCHANGE_STATUS.APPROVED);
  return EXCHANGE_STEP_LIST.indexOf(exchangeDetail.value.status) < approveStepIndex;
});
const isShowRequestApproval = computed(() => {
  return (
    exchangeDetail.value.status === EXCHANGE_STATUS.REQUESTED ||
    (exchangeDetail.value.status === EXCHANGE_STATUS.PENDING && approverList.value.length === 0)
  );
});
const isShowCancelRequestApproval = computed(() => {
  return (
    exchangeDetail.value.memberName === userStore.username &&
    exchangeDetail.value.status === EXCHANGE_STATUS.PENDING &&
    approverList.value.length > 0
  );
});
const isShowPrintDraft = computed(() => {
  return !isBeforeApproved.value;
});

const hqExchangeApi = new HQExchangeApi();

const getPageData = () => {
  hqExchangeApi.getExchangeDetail(exchangeCode).then(data => {
    exchangeDetail.value = data;
  });

  hqExchangeApi.getExchangeApproverList(exchangeCode).then(data => {
    approverList.value = data.map(e => ({ ...e, positionName: e.position, approved: e.approval }));
  });
};

const onCompleteApproval = () => {
  getPageData();
};

const handleRequestApproval = (approverCode, comment) => {
  hqExchangeApi
    .requestApproval({ exchangeCode, approved: DRAFTER_APPROVED.APPROVE, comment, approverCodeList: [approverCode] })
    .then(() => {
      toast.add({
        severity: 'success',
        summary: '처리 성공',
        detail: '교환에 대한 결재요청이 등록되었습니다.',
        life: 3000,
      });

      getPageData();
    });
};

const clickRequestApproval = () => {
  openModal({
    component: ApprovalRequestModalBody,
    header: '결재요청',
    data: {
      draftKind: DRAFT_KIND.EXCHANGE,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      const { approverCode, comment } = callbackParams;

      handleRequestApproval(approverCode, comment);
    },
  });
};

const cancelRequestApproval = () => {
  hqExchangeApi.cancelRequestApproval(exchangeCode).then(() => {
    toast.add({ severity: 'error', summary: '처리 성공', detail: '결재 요청이 취소되었습니다.', life: 3000 });

    // page reload
    getPageData();
  });
};

const clickCancelRequestApproval = () => {
  showConfirm({
    header: '결재 요청 취소',
    message: '결재 요청을 취소하시겠습니까?',
    acceptLabel: '결재 요청 취소',
    onAccept: cancelRequestApproval,
    danger: true,
  });
};

// 기안자가 교환요청 반려
const handleRejectDraft = comment => {
  hqExchangeApi.requestApproval({ exchangeCode, approved: DRAFTER_APPROVED.REJECT, comment }).then(() => {
    toast.add({
      severity: 'success',
      summary: '처리 성공',
      detail: '교환요청을 반려했습니다.',
      life: 3000,
    });

    // page reload
    getPageData();
  });
};
const clickRejectDraft = () => {
  openModal({
    component: DraftRejectModalBody,
    header: '교환요청 반려',
    data: {
      draftKind: DRAFT_KIND.EXCHANGE,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      const { comment } = callbackParams;

      handleRejectDraft(comment);
    },
  });
};

const clickPrintDraft = () => {
  printType.value = PRINT_TYPE.HQ.EXCHANGE_DRAFT;
  showPrintPdf.value = true;
};

const clickPrintInvoice = () => {
  printType.value = PRINT_TYPE.HQ.EXCHANGE_INVOICE;
  showPrintPdf.value = true;
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.exchange-detail-container {
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

  .title {
    align-self: center;
  }

  .content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }

  .approval-line-table {
    align-self: flex-end;
  }
}
</style>
