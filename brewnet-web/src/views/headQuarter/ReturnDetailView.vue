<template>
  <div v-if="returnDetail" class="return-detail-container">
    <div class="top-area">
      <Tag
        :value="formatKoReturnStatus(returnDetail.status)"
        :severity="getReturnStatusSeverity(returnDetail.status)"
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
          label="반품요청 반려"
          variant="outlined"
          severity="danger"
          size="small"
          @click="clickRejectDraft"
        />
        <Button
          label="반품요청서 출력"
          variant="outlined"
          size="small"
          :disabled="!isShowPrintDraft"
          @click="clickPrintDraft"
        />
        <Button
          label="거래취소명세서 출력"
          variant="outlined"
          size="small"
          :disabled="!enablePrintInvoice"
          @click="clickPrintInvoice"
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

    <h1 class="title">반품 요청서</h1>

    <DraftApprovalLine
      class="approval-line-table"
      :draft-manager-name="returnDetail.memberName"
      :approval-lines="approverList"
    />

    <ReturnDetailTable :return-detail="returnDetail" />

    <div v-if="returnDetail.returningImageList.length > 0">
      <AppLabel use-margin-bottom label="반품 상품 사진" />
      <AppImageList :images="returnDetail.returningImageList" />
    </div>

    <AppLabelText v-if="returnDetail.memberName" label="기안자 비고사항" :text="returnDetail.comment || '(비어있음)'" />

    <DraftApprovalHistoryTable
      :approval-lines="approverList"
      :draft-kind="DRAFT_KIND.RETURN"
      :draft-code="returnCode"
      @complete-approval="onCompleteApproval"
    />

    <DynamicDialog />
    <PrintReturnPdfPreviewModal v-model:show="showPrintPdf" :return-detail="returnDetail" :print-type="printType" />
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
import PrintReturnPdfPreviewModal from '@/components/headQuarter/PrintReturnPdfPreviewModal.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQReturnApi from '@/utils/api/HQReturnApi';
import { DRAFT_KIND, DRAFTER_APPROVED, PRINT_TYPE, RETURN_STATUS, RETURN_STEP_LIST } from '@/utils/constant';
import { formatKoReturnStatus } from '@/utils/format';
import { getReturnStatusSeverity } from '@/utils/helper';

import ReturnDetailTable from './ReturnDetailTable.vue';

const ApprovalRequestModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/ApprovalRequestModalBody.vue'),
);
const DraftRejectModalBody = defineAsyncComponent(() => import('@/components/headQuarter/DraftRejectModalBody.vue'));

const route = useRoute();
const { returnCode } = route.params;
const router = useRouter();
const { openModal } = useModal();
const toast = useToast();
const { showConfirm } = useAppConfirmModal();
const userStore = useUserStore();

const returnDetail = ref(null);
const approverList = ref([]);
const showPrintPdf = ref(false);
const printType = ref(PRINT_TYPE.HQ.RETURN_DRAFT);

const isBeforeApproved = computed(() => {
  const approveStepIndex = RETURN_STEP_LIST.indexOf(RETURN_STATUS.APPROVED);
  return RETURN_STEP_LIST.indexOf(returnDetail.value.status) < approveStepIndex;
});
const isShowRequestApproval = computed(() => {
  // 신규요청 상태이거나
  // 결재라인이 아무것도 없을 때 (결재취소)
  return (
    returnDetail.value.status === RETURN_STATUS.REQUESTED ||
    (returnDetail.value.status === RETURN_STATUS.PENDING && approverList.value.length === 0)
  );
});
const isShowCancelRequestApproval = computed(() => {
  return (
    returnDetail.value.memberName === userStore.username &&
    returnDetail.value.status === RETURN_STATUS.PENDING &&
    approverList.value.length > 0
  );
});
const isShowPrintDraft = computed(() => {
  // 승인 이후 단계면 출력 가능
  return !isBeforeApproved.value;
});
const enablePrintInvoice = computed(() => {
  return !isBeforeApproved.value;
});

const hqReturnApi = new HQReturnApi();

const getPageData = () => {
  // 반품 상세 조회
  hqReturnApi.getReturnDetail(returnCode).then(data => {
    returnDetail.value = data;
  });

  // 결재자 목록 조회
  hqReturnApi.getReturnApproverList(returnCode).then(data => {
    approverList.value = data.map(e => ({ ...e, positionName: e.position, approved: e.approval }));
  });
};

const onCompleteApproval = () => {
  getPageData();
};

const handleRequestApproval = (approverCode, comment) => {
  hqReturnApi
    .requestApproval({ returnCode, approved: DRAFTER_APPROVED.APPROVE, comment, approverCodeList: [approverCode] })
    .then(() => {
      toast.add({
        severity: 'success',
        summary: '처리 성공',
        detail: '반품에 대한 결재요청이 등록되었습니다.',
        life: 3000,
      });

      // page reload
      getPageData();
    });
};

const clickRequestApproval = () => {
  openModal({
    component: ApprovalRequestModalBody,
    header: '결재요청',
    data: {
      draftKind: DRAFT_KIND.RETURN,
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
  hqReturnApi.cancelRequestApproval(returnCode).then(() => {
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

// 기안자가 반품요청 반려
const handleRejectDraft = comment => {
  hqReturnApi.requestApproval({ returnCode, approved: DRAFTER_APPROVED.REJECT, comment }).then(() => {
    toast.add({
      severity: 'success',
      summary: '처리 성공',
      detail: '반품요청을 반려했습니다.',
      life: 3000,
    });

    // page reload
    getPageData();
  });
};
const clickRejectDraft = () => {
  openModal({
    component: DraftRejectModalBody,
    header: '반품요청 반려',
    data: {
      draftKind: DRAFT_KIND.RETURN,
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
  printType.value = PRINT_TYPE.HQ.RETURN_DRAFT;
  showPrintPdf.value = true;
};

const clickPrintInvoice = () => {
  printType.value = PRINT_TYPE.HQ.RETURN_INVOICE;
  showPrintPdf.value = true;
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.return-detail-container {
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
