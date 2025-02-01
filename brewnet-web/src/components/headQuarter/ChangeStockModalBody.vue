<template>
  <AppModalBody action-button-label="저장" @click-action-button="clickSave">
    <AppInputText v-model="quantity" label="조정 수량" label-position="left" full-width placeholder="숫자만 입력" />
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, inject, ref } from 'vue';

import HQStorageApi from '@/utils/api/HQStorageApi';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';

const toast = useToast();
const hqStorageApi = new HQStorageApi();

const dialogRef = inject('dialogRef');
const selectedStorage = ref(null);
const selectedItem = ref(null);
const quantity = ref(null);

const checkForm = () => {
  try {
    if (!quantity.value) throw new Error('재고에 합산할 수량을 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickSave = () => {
  const isPass = checkForm();
  if (!isPass) return;

  hqStorageApi
    .changeStock({ storageCode: selectedStorage.value, itemCode: selectedItem.value, quantity: quantity.value })
    .then(() => {
      dialogRef.value.close({
        reload: true,
      });
    });
};

onMounted(() => {
  const { storageCode, itemCode } = dialogRef.value.data;
  selectedStorage.value = storageCode;
  selectedItem.value = itemCode;
});
</script>

<style scoped></style>
