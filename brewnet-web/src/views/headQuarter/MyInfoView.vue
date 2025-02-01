<template>
  <div class="my-info-container">
    <template v-if="memberDetail">
      <AppLabelText label="아이디" :text="memberDetail.id" />
      <AppLabelText label="성명" :text="memberDetail.name" />
      <AppLabelText label="이메일" :text="memberDetail.email" />
      <AppLabelText label="휴대폰번호" :text="memberDetail.contact" />
      <AppLabelText label="직위" :text="formatKoEmployeePosition(memberDetail.positionName)" />
      <AppLabelText label="시스템 권한" :text="formatKoMemberRole(memberDetail.role)" />

      <template v-if="memberDetail.signatureUrl">
        <div class="signature">
          <AppLabel label="서명" />
          <Image :src="memberDetail.signatureUrl" alt="내 서명 이미지" width="200" preview />
          <Button
            label="서명 삭제"
            size="small"
            severity="secondary"
            variant="outlined"
            @click="clickRemoveSignature"
          />
        </div>
      </template>
      <template v-else>
        <AppLabelText label="서명" text="등록된 서명이 없습니다." />
      </template>
    </template>

    <!-- 서명 삭제 Modal -->
    <Dialog v-model:visible="confirmModalDeleteSignature" modal header="서명 삭제" :draggable="false">
      <p class="mb-4">서명을 삭제하시려면, 현재 비밀번호를 입력해주세요.</p>

      <AppInputPassword v-model="currentPassword" placeholder="현재 비밀번호 입력" class="mb-2" full-width />
      <Button
        label="서명 삭제"
        size="small"
        :disabled="!currentPassword"
        severity="danger"
        fluid
        @click="deleteSignature"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';

import AppLabel from '@/components/common/AppLabel.vue';
import AppLabelText from '@/components/common/AppLabelText.vue';
import AppInputPassword from '@/components/common/form/AppInputPassword.vue';
import MemberApi from '@/utils/api/MemberApi';
import { formatKoEmployeePosition, formatKoMemberRole } from '@/utils/format';

const toast = useToast();

const memberDetail = ref(null);
const confirmModalDeleteSignature = ref(false);
const currentPassword = ref('');

const memberApi = new MemberApi();

const getMyInfo = () => {
  memberApi.getMyInfo().then(data => {
    memberDetail.value = data;
  });
};

const deleteSignature = () => {
  // 1. 삭제 전 본인확인 진행
  memberApi.checkAuthByPassword(currentPassword.value).then(uuid => {
    // 2. 서명 삭제 진행
    memberApi.deleteMySignature(uuid).then(() => {
      toast.add({ severity: 'success', summary: '처리 성공', detail: '서명이 삭제되었습니다.', life: 3000 });
      confirmModalDeleteSignature.value = false;
      // reload page data
      getMyInfo();
    });
  });
};

const clickRemoveSignature = () => {
  confirmModalDeleteSignature.value = true;
};

onMounted(() => {
  // 내 정보 조회
  getMyInfo();
});
</script>

<style scoped>
.my-info-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: flex-start;

  .signature {
    display: flex;
    flex-direction: column;
    gap: 5px;

    .image {
      width: 70px;
      height: 70px;
      object-fit: contain;
    }
  }
}
</style>
