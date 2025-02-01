<template>
  <div v-if="purchaseDetail" class="purchase-detail-container">
    <div class="top-area">
      <Tag
        rounded
        :value="formatKoApprovalStatus(purchaseDetail.allApproved)"
        :severity="getApprovalStatusSeverity(purchaseDetail.allApproved)"
        class="mb-1"
      />

      <div class="top-buttons">
        <Button
          label="발주서 출력"
          variant="outlined"
          size="small"
          :disabled="!isApproved"
          @click="clickPrintPurchaseDocument"
        />
        <Button
          label="구매품의서 전송(회계)"
          variant="outlined"
          size="small"
          :disabled="isAlreadySend || !isApproved"
          @click="clickSendPurchase"
        />
        <Button
          label="구매품의서 출력"
          variant="outlined"
          size="small"
          :disabled="!isApproved"
          @click="clickPrintPurchase"
        />
        <Button
          v-if="canDelete"
          label="삭제"
          severity="danger"
          size="small"
          variant="outlined"
          :disabled="disabledDeleteButton"
          @click="clickDelete"
        />
      </div>
    </div>

    <div class="body-area">
      <h1>구매품의서</h1>

      <DraftApprovalLine
        class="approval-line-table"
        :draft-manager-name="purchaseDetail.memberName"
        :approval-lines="purchaseApprovalLines"
      />

      <AppTableStyled full-width>
        <tbody>
          <tr>
            <th>거래처</th>
            <td class="align-center">{{ purchaseDetail.correspondentName }}</td>
            <th>입고창고</th>
            <td class="align-center">{{ purchaseDetail.storageName }}</td>
            <th>발주일자</th>
            <td class="align-center">{{ purchaseDetail.createdAt }}</td>
            <th>담당자</th>
            <td class="align-center" colspan="2">{{ purchaseDetail.memberName }}</td>
          </tr>
          <tr>
            <th :rowspan="purchaseDetail.items.length + 2">발주상품</th>
            <th>상품코드</th>
            <th colspan="3">상품명</th>
            <th>수량</th>
            <th>단가</th>
            <th>공급가액</th>
            <th>부가세</th>
          </tr>
          <tr v-for="item in purchaseDetail.items" :key="item.itemCode">
            <td class="align-center">{{ item.itemCode }}</td>
            <td colspan="3">{{ item.itemName }}</td>
            <td class="align-center">{{ item.quantity.toLocaleString() }}</td>
            <td class="align-right">{{ item.purchasePrice.toLocaleString() }}</td>
            <td class="align-right">{{ item.purchaseSum.toLocaleString() }}</td>
            <td class="align-right">{{ item.purchaseVat.toLocaleString() }}</td>
          </tr>
          <tr>
            <th>총 발주금액</th>
            <td colspan="5" class="align-center">{{ purchaseDetail.totalPrice.toLocaleString() }}</td>
            <td class="align-right">{{ purchaseDetail.sumPrice.toLocaleString() }}</td>
            <td class="align-right">{{ purchaseDetail.vatPrice.toLocaleString() }}</td>
          </tr>
          <tr style="height: 100px">
            <th>첨언</th>
            <td colspan="8" class="align-center">{{ purchaseDetail.memberComment }}</td>
          </tr>
        </tbody>
      </AppTableStyled>
    </div>

    <DraftApprovalHistoryTable
      :approval-lines="purchaseApprovalLines"
      :draft-kind="DRAFT_KIND.PURCHASE"
      :draft-code="purchaseCode"
      @complete-approval="onCompleteApproval"
    />

    <DynamicDialog />

    <PrintPurchasePdfPreviewModal v-model:show="showPrintPdf" :purchase-detail="purchaseDetail" />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref, defineAsyncComponent } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import DraftApprovalHistoryTable from '@/components/headQuarter/DraftApprovalHistoryTable.vue';
import DraftApprovalLine from '@/components/headQuarter/DraftApprovalLine.vue';
import PrintPurchasePdfPreviewModal from '@/components/headQuarter/PrintPurchasePdfPreviewModal.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import { APPROVAL_STATUS, DRAFT_KIND } from '@/utils/constant';
import { formatKoApprovalStatus } from '@/utils/format';
import { getApprovalStatusSeverity } from '@/utils/helper';
import LocalStorageUtil from '@/utils/localStorage';

const route = useRoute();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();
const userStore = useUserStore();

const showPrintPdf = ref(false);
const purchaseDetail = ref(null);
const purchaseApprovalLines = ref([]);
const disabledDeleteButton = computed(() => {
  return purchaseDetail.value.allApproved !== APPROVAL_STATUS.UNCONFIRMED;
});
const isAlreadySend = ref(false);
const isApproved = computed(() => {
  return purchaseDetail.value.allApproved === APPROVAL_STATUS.APPROVED;
});

const PrintPurposeModalBody = defineAsyncComponent(() => import('@/components/headQuarter/PrintPurposeModalBody.vue'));

const localStorageUtil = new LocalStorageUtil();
const hqPurchaseApi = new HQPurchaseApi();
const { purchaseCode } = route.params;
const { openModal } = useModal();

const clickPrintPurchaseDocument = () => {
  openModal({
    component: PrintPurposeModalBody,
    header: '발주서 출력 용도를 선택해 주세요.',
  });
};

const clickSendPurchase = () => {
  localStorageUtil.saveSendCompletePurchase(purchaseCode);

  toast.add({
    severity: 'success',
    summary: '처리 성공',
    detail: '구매품의서가 회계부서로 전송되었습니다.',
    life: 3000,
  });

  // 보냈다고 가정
  isAlreadySend.value = true;
};

const clickPrintPurchase = () => {
  showPrintPdf.value = true;
};

const canDelete = computed(() => {
  return userStore.username === purchaseDetail.value.memberName;
});

const deletePurchase = () => {
  hqPurchaseApi.deletePurchase(purchaseCode).then(() => {
    toast.add({ severity: 'error', summary: '처리 성공', detail: '구매품의서가 삭제되었습니다.', life: 3000 });
    router.replace({ name: 'hq:purchase:list' });
  });
};

const clickDelete = () => {
  showConfirm({
    header: '발주 삭제',
    message: '구매품의서를 삭제하시겠습니까?',
    acceptLabel: '발주 삭제',
    onAccept: deletePurchase,
    danger: true,
  });
};

const getPurchaseDetail = () => {
  hqPurchaseApi.getPurchase(purchaseCode).then(data => {
    purchaseDetail.value = data;
  });
};

const getPurchaseApprovalLines = () => {
  hqPurchaseApi.getPurchaseApprovalLines(purchaseCode).then(data => {
    purchaseApprovalLines.value = data.approvers.map(e => ({
      ...e,
      createdAt: e.approvedAt,
    }));
  });
};

const getPurchaseDetailPageData = () => {
  getPurchaseDetail();
  getPurchaseApprovalLines();
};

// 결재 승인/반려 완료 시
const onCompleteApproval = () => {
  getPurchaseDetailPageData();
};

onMounted(() => {
  if (!purchaseCode) return;

  // 발주 상세 데이터 셋팅
  getPurchaseDetailPageData();

  // 이미 회계팀에 보냈는지 확인
  isAlreadySend.value = localStorageUtil.isSendCompletePurchase(Number(purchaseCode));
});
</script>

<style scoped>
.purchase-detail-container {
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
