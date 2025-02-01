<template>
  <AppModalBody
    :action-button-label="`${formatKoDraftKind(currentDraftKind)}요청 반려`"
    @click-action-button="clickReject"
  >
    <AppInputText v-model="comment" label="비고사항" label-position="left" full-width />
  </AppModalBody>
</template>

<script setup>
import { inject, onMounted, ref } from 'vue';

import { formatKoDraftKind } from '@/utils/format';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';

const dialogRef = inject('dialogRef');

const currentDraftKind = ref(null);
const comment = ref('');

const clickReject = () => {
  dialogRef.value.close({
    comment: comment.value,
  });
};

onMounted(() => {
  const { draftKind } = dialogRef.value.data;
  currentDraftKind.value = draftKind;
});
</script>

<style scoped>
.approval-line {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}
</style>
