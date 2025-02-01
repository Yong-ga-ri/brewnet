<template>
  <AuthorizationRequiredArea v-model="isAuthorized" @authenticated="onAuthenticated">
    <div class="signature-upload-container">
      <AppChooseFile @change-file="onChangeFile" />

      <div v-if="previewUrl" class="uploaded-area">
        <img v-if="previewUrl" :src="previewUrl" alt="업로드 된 이미지" />
        <Button size="small" label="저장" @click="saveImage" />
      </div>
    </div>
  </AuthorizationRequiredArea>
</template>

<script setup>
import { useToast } from 'primevue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import AuthorizationRequiredArea from '@/components/common/AuthorizationRequiredArea.vue';
import AppChooseFile from '@/components/common/form/AppChooseFile.vue';
import { useFile } from '@/hooks/useFile';
import MemberApi from '@/utils/api/MemberApi';

const router = useRouter();
const toast = useToast();
const { previewUrl, file: signatureFile, onChangeFile } = useFile();

const isAuthorized = ref(false);

let checkNum = null;
const memberApi = new MemberApi();

const saveImage = () => {
  memberApi.changeMySignature(signatureFile.value, checkNum).then(() => {
    toast.add({ severity: 'success', summary: '성공', detail: '서명이 등록되었습니다.', life: 3000 });
    router.replace({ name: 'hq:my:info' });
  });
};

const onAuthenticated = uuid => {
  checkNum = uuid;
};
</script>

<style scoped>
.signature-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  .uploaded-area {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;

    img {
      width: 250px;
      height: 250px;
      object-fit: contain;
    }
  }
}
</style>
