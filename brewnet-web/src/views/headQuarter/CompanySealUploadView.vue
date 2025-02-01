<template>
  <div class="company-seal-upload-container">
    <AppChooseFile @change-file="onChangeFile" />

    <div v-if="previewUrl" class="uploaded-area">
      <img v-if="previewUrl" :src="previewUrl" alt="업로드 된 이미지" />
      <Button size="small" label="저장" @click="clickSave" />
    </div>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { useRouter } from 'vue-router';

import AppChooseFile from '@/components/common/form/AppChooseFile.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useFile } from '@/hooks/useFile';
import MasterCompanyApi from '@/utils/api/MasterCompanyApi';

const router = useRouter();
const toast = useToast();
const { showConfirm } = useAppConfirmModal();
const { previewUrl, file: imageFile, onChangeFile } = useFile();

const masterCompanyApi = new MasterCompanyApi();

const saveImage = () => {
  masterCompanyApi.editCompanySeal(imageFile.value).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '법인인감이 수정되었습니다.', life: 3000 });
    router.replace({ name: 'hq:settings:company-seal' });
  });
};

const clickSave = () => {
  showConfirm({
    header: '법인인감 변경',
    message: '법인인감 이미지를 변경할까요?',
    acceptLabel: '변경',
    danger: true,
    onAccept: saveImage,
  });
};
</script>

<style scoped>
.company-seal-upload-container {
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
