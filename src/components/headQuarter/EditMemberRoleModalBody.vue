<template>
  <AppModalBody action-button-label="저장" @click-action-button="clickSave">
    <template v-if="currentMember">
      <p class="mb-3">[{{ currentMember.name }}] 임직원의 권한을 변경합니다.</p>
      <AppSelect v-model="selectedRole" :options="roleOptions" :initial-value="initialRole" />
    </template>
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, inject, onMounted, ref } from 'vue';

import AppModalBody from '@/components/common/AppModalBody.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import MasterAuhApi from '@/utils/api/MasterAuthApi';
import { HEAD_QUARTER_ROLES } from '@/utils/constant';
import { formatKoMemberRole } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

const dialogRef = inject('dialogRef');

const toast = useToast();

const currentMember = ref(null);
const selectedRole = ref('');
const initialRole = computed(() => {
  return currentMember.value?.role || '';
});
const roleOptions = computed(() => {
  const headQuarterRoleOptions = HEAD_QUARTER_ROLES.map(e => makeSelectOption(formatKoMemberRole(e), e));

  // 현재 권한이 있는 경우에만 "권한 없음" 옵션 표시
  if (currentMember.value?.role) {
    return headQuarterRoleOptions.concat(makeSelectOption('권한 없음', 'null'));
  }

  return headQuarterRoleOptions;
});

const masterAuthApi = new MasterAuhApi();

const clickSave = () => {
  const changeToRole = selectedRole.value === 'null' ? null : selectedRole.value;

  masterAuthApi.setMemberRole(currentMember.value.id, changeToRole).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '권한이 변경되었습니다.', life: 3000 });
    dialogRef.value.close({
      reload: true,
    });
  });
};

onMounted(() => {
  // 현재 member 권한 가져오기
  const { member } = dialogRef.value.data;
  currentMember.value = member;
});
</script>

<style scoped></style>
