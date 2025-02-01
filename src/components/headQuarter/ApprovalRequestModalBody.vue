<template>
  <AppModalBody action-button-label="결재요청" @click-action-button="clickRequest">
    <div class="approval-line mb-4">
      <AppInputText
        :model-value="`${formatKoDraftKind(currentDraftKind)} 결재라인`"
        label="결재라인"
        disabled
        label-position="left"
      />
      <AppAutoComplete
        v-model="selectedApprovalUser"
        :suggestions="approvalUserSuggestions"
        full-width
        placeholder="임직원명으로 검색"
        @complete-input="onCompleteInputApprovalUser"
      />
    </div>

    <AppInputText v-model="comment" label="비고사항" label-position="left" full-width />
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, inject, onMounted, ref } from 'vue';

import HQDocumentApi from '@/utils/api/HQDocumentApi';
import { formatKoDraftKind } from '@/utils/format';
import { makeAutocompleteSuggestion } from '@/utils/helper';

import AppModalBody from '../common/AppModalBody.vue';
import AppAutoComplete from '../common/form/AppAutoComplete.vue';
import AppInputText from '../common/form/AppInputText.vue';

const toast = useToast();

const dialogRef = inject('dialogRef');
const currentDraftKind = ref(null);

const selectedApprovalUser = ref(null);
const approverCandidates = ref([]);
const approvalUserSuggestions = computed(() => {
  return approverCandidates.value.map(e => makeAutocompleteSuggestion(e.approverCode, e.approverName));
});
const comment = ref('');

const hqDocumentApi = new HQDocumentApi();

const onCompleteInputApprovalUser = event => {
  if (!currentDraftKind.value) return;

  // 결재자 후보 목록 조회
  hqDocumentApi.getApproverCandidates(currentDraftKind.value).then(data => {
    approverCandidates.value = data;
  });
};

const checkForm = () => {
  try {
    if (!selectedApprovalUser.value) throw new Error('결재자를 선택해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickRequest = () => {
  const isPass = checkForm();
  if (!isPass) return;

  dialogRef.value.close({
    approverCode: selectedApprovalUser.value.code,
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
