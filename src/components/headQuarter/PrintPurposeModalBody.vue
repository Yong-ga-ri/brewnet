<template>
  <AppModalBody action-button-label="출력" @click-action-button="clickPrint">
    <div class="top-buttons">
      <Button
        label="내부용"
        :variant="forExport === true ? 'outlined' : 'filled'"
        size="small"
        @click="clickChangeForInHouse"
      />
      <Button
        label="외부용"
        :variant="forExport === true ? 'filled' : 'outlined'"
        size="small"
        @click="clickChangeForExport"
      />
    </div>

    <AppInputText
      v-if="forExport"
      v-model="reason"
      label="출력 사유"
      label-position="left"
      full-width
      placeholder="사유 입력"
    />
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, inject, ref, defineAsyncComponent } from 'vue';
import { useRoute } from 'vue-router';

import { useModal } from '@/hooks/useModal';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';

const route = useRoute();
const toast = useToast();
const hqPurchaseApi = new HQPurchaseApi();

const PrintPurchaseDocumentPreviewModal = defineAsyncComponent(
  () => import('@/components/headQuarter/PrintPurchaseDocumentPreviewModal.vue'),
);

const PrintInHouseDocumentPreviewModal = defineAsyncComponent(
  () => import('@/components/headQuarter/PrintInHouseDocumentPreviewModal.vue'),
);

const { purchaseCode } = route.params;
const { openModal } = useModal();
const dialogRef = inject('dialogRef');
const reason = ref(null);
const forExport = ref(false);

const clickChangeForInHouse = () => {
  forExport.value = false;
};

const clickChangeForExport = () => {
  forExport.value = true;
};

const checkForm = () => {
  try {
    if (forExport.value && !reason.value) throw new Error('출력 사유를 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickPrint = () => {
  const isPass = checkForm();
  if (!isPass) return;

  if (forExport.value) {
    hqPurchaseApi.createPrintRecord(purchaseCode, { reason: reason.value }).then(() => {
      dialogRef.value.close();
      openModal({
        component: PrintPurchaseDocumentPreviewModal,
      });
    });
  } else if (!forExport.value) {
    dialogRef.value.close();
    openModal({
      component: PrintInHouseDocumentPreviewModal,
    });
  }
};
</script>

<style scoped>
.top-buttons {
  display: flex;
  gap: 5px;
  margin-bottom: 20px;
}
</style>
