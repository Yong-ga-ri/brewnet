<template>
  <AppModalBody action-button-label="저장" @click-action-button="clickSave">
    <AppRadioList
      v-model="approved"
      name="approver-approved-status"
      label="결재형태"
      :options="approvedOptions"
      label-position="left"
      class="mb-6"
    />
    <AppInputText v-model="comment" label="첨언" label-position="left" full-width />
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, inject, ref } from 'vue';

import { APPROVER_APPROVED_STATUS } from '@/utils/constant';
import { formatKoApproverApprovedStatus } from '@/utils/format';
import { makeRadioOption } from '@/utils/helper';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';
import AppRadioList from '../common/form/AppRadioList.vue';

const toast = useToast();

const dialogRef = inject('dialogRef');
const approvedOptions = computed(() => {
  return [APPROVER_APPROVED_STATUS.APPROVED, APPROVER_APPROVED_STATUS.REJECTED].map(e =>
    makeRadioOption(formatKoApproverApprovedStatus(e), e, e),
  );
});
const approved = ref('');
const comment = ref('');

const checkForm = () => {
  try {
    if (!approved.value) throw new Error('결재형태를 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickSave = () => {
  const isPass = checkForm();
  if (!isPass) return;

  dialogRef.value.close({
    approved: approved.value,
    comment: comment.value,
  });
};
</script>

<style scoped></style>
