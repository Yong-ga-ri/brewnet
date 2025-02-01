<template>
  <form class="storage-form-container" @submit.prevent="onSubmit">
    <AppInputText v-model="name" label="창고명" />

    <!-- 주소 검색 -->
    <div>
      <AppInputText
        :model-value="address"
        label="주소"
        placeholder="주소검색"
        read-only
        class="mb-2"
        icon="pi pi-search"
        full-width
        icon-position="end"
        @on-click="onClickSearchAddress"
      />
    </div>

    <AppInputText v-model="contact" label="연락처" placeholder="-없이 숫자만 입력" />

    <Button type="submit" label="저장" size="small" />

    <DynamicDialog />
  </form>
</template>

<script setup>
import { useToast } from 'primevue';
import { defineAsyncComponent, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppInputText from '@/components/common/form/AppInputText.vue';
import { useModal } from '@/hooks/useModal';
import HQStorageApi from '@/utils/api/HQStorageApi';
import { notNumber } from '@/utils/regex';

const SearchAddressModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/SearchAddressModalBody.vue'),
);

const toast = useToast();
const { openModal } = useModal();
const route = useRoute();
const storageCode = route.params.storageCode;
const router = useRouter();

const name = ref('');
const address = ref('');
const contact = ref('');
const editMode = ref(false);

const hqStorageApi = new HQStorageApi();

const onClickSearchAddress = () => {
  openModal({
    component: SearchAddressModalBody,
    header: '주소 검색',
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams || !callbackParams.selectedAddress) return;

      // 검색결과로 얻은 주소
      address.value = callbackParams.selectedAddress;
    },
  });
};

const checkForm = () => {
  try {
    if (!name.value) throw new Error('창고명을 입력해주세요.');
    if (!address.value) throw new Error('주소검색을 통해 창고 주소를 입력해주세요.');
    if (!contact.value) throw new Error('창고 연락처를 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const onSubmit = async () => {
  const isPass = checkForm();
  if (!isPass) return;

  let successMsg = '';

  const requestBody = {
    name: name.value,
    address: address.value,
    contact: contact.value.replace(notNumber, ''),
  };

  if (editMode.value) {
    requestBody.storageCode = storageCode;
    await hqStorageApi.editStorage(storageCode, requestBody);
    successMsg = '창고 정보가 변경되었습니다.';
  } else {
    await hqStorageApi.createStorage(requestBody);
    successMsg = '창고가 등록되었습니다.';
  }

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.replace({ name: 'hq:stock:storage:list' });
};

watch(
  () => route.params.storageCode,
  newVal => {
    if (newVal) {
      editMode.value = true;

      // 수정모드인 경우 기본 값 설정
      hqStorageApi.getStorage(storageCode).then(data => {
        name.value = data.storageName;
        address.value = data.address;
        contact.value = data.contact;
      });
    } else {
      // 생성모드는 값 초기화
      editMode.value = false;

      name.value = '';
      address.value = '';
      contact.value = '';
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.storage-form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 300px;
}
</style>
