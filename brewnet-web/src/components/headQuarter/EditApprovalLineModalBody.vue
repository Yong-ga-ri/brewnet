<template>
  <AppModalBody action-button-label="저장" :disabled-action-button="!position" @click-action-button="save">
    <AppSelect v-model="position" label="직위" :options="positionOptions" :initial-value="initialPosition" />
  </AppModalBody>
</template>

<script setup>
import { computed, inject, onMounted, ref } from 'vue';

import MasterDocumentApi from '@/utils/api/MasterDocumentApi';
import { APPROVAL_POSITIONS } from '@/utils/constant';
import DOMEvent from '@/utils/domEvent';
import { formatKoEmployeePosition } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

import AppModalBody from '../common/AppModalBody.vue';
import AppSelect from '../common/form/AppSelect.vue';

const emit = defineEmits(['complete']);

const dialogRef = inject('dialogRef');

const position = ref('');
const initialPosition = ref('');
const positionOptions = computed(() => {
  return APPROVAL_POSITIONS.map(e => makeSelectOption(formatKoEmployeePosition(e), e));
});

let approvalLine = null;
const masterDocumentApi = new MasterDocumentApi();

const save = () => {
  if (!approvalLine) return;

  masterDocumentApi.setApprovalLine({ ...approvalLine, positionName: position.value }).then(() => {
    DOMEvent.dispatchApiSuccess('결재라인이 수정되었습니다.');
    dialogRef.value.close({ reload: true });
  });
};

onMounted(() => {
  const { targetApprovalLine } = dialogRef.value.data;
  approvalLine = targetApprovalLine;

  // 현재 설정된 결재라인이 있다면
  if (targetApprovalLine.positionName) {
    initialPosition.value = targetApprovalLine.positionName;
    position.value = targetApprovalLine.positionName;
  }
});
</script>

<style scoped></style>
