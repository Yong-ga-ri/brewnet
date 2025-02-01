<template>
  <form class="correspondent-form-container" @submit.prevent="onSubmit">
    <AppInputText v-model="name" label="거래처명" />

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
      <AppInputText v-model="addressDetail" placeholder="상세주소 입력" />
    </div>

    <AppInputText v-model="manager" label="담당자명" />
    <AppInputText v-model="email" label="이메일" />
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
import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';
import { notNumber } from '@/utils/regex';

const SearchAddressModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/SearchAddressModalBody.vue'),
);

const toast = useToast();
const { openModal } = useModal();
const route = useRoute();
const correspondentCode = route.params.correspondentCode;
const router = useRouter();

const name = ref('');
const address = ref('');
const addressDetail = ref('');
const email = ref('');
const contact = ref('');
const manager = ref('');
const editMode = ref(false);

const hqCorrespondentApi = new HQCorrespondentApi();

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
    if (!name.value) throw new Error('거래처명을 입력해주세요.');
    if (!address.value) throw new Error('주소검색을 통해 거래처 주소를 입력해주세요.');

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
    detailAddress: addressDetail.value,
    email: email.value,
    contact: contact.value.replace(notNumber, ''),
    managerName: manager.value,
  };

  if (editMode.value) {
    requestBody.correspondentCode = correspondentCode;
    await hqCorrespondentApi.editCorrespondent(correspondentCode, requestBody);
    successMsg = '거래처 정보가 변경되었습니다.';
  } else {
    await hqCorrespondentApi.createCorrespondent(requestBody);
    successMsg = '거래처가 등록되었습니다.';
  }

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.push({ name: 'hq:partner:correspondent:list' });
};

watch(
  () => route.params.correspondentCode,
  newVal => {
    if (newVal) {
      editMode.value = true;

      // 수정모드인 경우 기본 값 설정
      hqCorrespondentApi.getCorrespondent(correspondentCode).then(data => {
        name.value = data.correspondentName;
        address.value = data.address;
        addressDetail.value = data.detailAddress;
        email.value = data.email;
        contact.value = data.contact;
        manager.value = data.managerName;
      });
    } else {
      // 생성모드는 값 초기화
      editMode.value = false;

      name.value = '';
      address.value = '';
      addressDetail.value = '';
      email.value = '';
      contact.value = '';
      manager.value = '';
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.correspondent-form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 300px;
}
</style>
