<template>
  <AppModalBody action-button-label="저장" @click-action-button="clickSaveAdd">
    <AppSelect
      v-model="selectedSuperCategory"
      label="상위카테고리"
      :options="superCateOptions"
      class="mb-3"
      helper-text="미입력 시, 상위 카테고리로 생성됩니다."
      show-clear
    />
    <AppInputText v-model="addingCategoryName" label="카테고리명" />

    <template #footer>
      <Button label="저장" @click="clickSaveAdd" />
    </template>
  </AppModalBody>
</template>

<script setup>
import { inject, onMounted, ref } from 'vue';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';
import AppSelect from '../common/form/AppSelect.vue';

const dialogRef = inject('dialogRef');

const addingCategoryName = ref('');
const selectedSuperCategory = ref('');
const superCateOptions = ref([]);

const clickSaveAdd = () => {
  if (!addingCategoryName.value) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: '카테고리명을 입력해주세요.', life: 3000 });
    return;
  }

  dialogRef.value.close({
    selectedSuperCategory: selectedSuperCategory.value,
    addingCategoryName: addingCategoryName.value,
  });
};

onMounted(() => {
  const { superCategoryOptions } = dialogRef.value.data;
  superCateOptions.value = superCategoryOptions;
});
</script>

<style scoped></style>
