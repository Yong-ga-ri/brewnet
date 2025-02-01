<template>
  <form class="franchise-form-container" @submit.prevent="onSubmit">
    <AppInputText v-model="franchiseName" label="지점명" />

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

    <AppInputText v-model="contact" label="가맹점 연락처" placeholder="-없이 숫자만 입력" />
    <AppInputText v-model="businessNumber" label="사업자등록번호" placeholder="-없이 숫자만 입력" />
    <AppInputText v-model="ceo" label="대표자명" />

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
import HQFranchiseApi from '@/utils/api/HQFranchiseApi';
import ResponsibleFranchiseApi from '@/utils/api/ResponsibleFranchiseApi';
import { mockupFranchises } from '@/utils/mockup';
import { notNumber } from '@/utils/regex';

const SearchAddressModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/SearchAddressModalBody.vue'),
);

const toast = useToast();
const { openModal } = useModal();
const route = useRoute();
const franchiseCode = route.params.franchiseCode;
const router = useRouter();

const franchiseName = ref('');
const address = ref('');
const addressDetail = ref('');
const contact = ref('');
const businessNumber = ref('');
const ceo = ref('');
const editMode = ref(false);

const responsibleFranchiseApi = new ResponsibleFranchiseApi();
const hqFranchiseApi = new HQFranchiseApi();

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
    if (!franchiseName.value) throw new Error('지점명을 입력해주세요.');
    if (!address.value) throw new Error('주소검색을 통해 가맹점 주소를 입력해주세요.');
    if (!contact.value) throw new Error('가맹점 연락처를 입력해주세요.');
    if (!businessNumber.value) throw new Error('사업자등록번호를 입력해주세요.');
    if (!ceo.value) throw new Error('대표자명을 입력해주세요.');

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
    franchiseName: franchiseName.value,
    address: address.value,
    detailAddress: addressDetail.value,
    contact: contact.value.replace(notNumber, ''),
    businessNumber: businessNumber.value.replace(notNumber, ''),
    name: ceo.value,
  };

  if (editMode.value) {
    requestBody.franchiseCode = franchiseCode;
    await responsibleFranchiseApi.editFranchise(requestBody);
    successMsg = '가맹점 정보가 변경되었습니다.';
  } else {
    await responsibleFranchiseApi.createFranchise(requestBody);
    successMsg = '가맹점이 등록되었습니다.';
  }

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.push({ name: 'hq:partner:franchise:list' });
};

watch(
  () => route.params.franchiseCode,
  newVal => {
    if (newVal) {
      editMode.value = true;

      // 수정모드인 경우 기본 값 설정
      hqFranchiseApi.getFranchise(franchiseCode).then(data => {
        franchiseName.value = data.franchiseName;
        address.value = data.address;
        addressDetail.value = data.detailAddress;
        contact.value = data.contact;
        businessNumber.value = data.businessNumber;
        ceo.value = data.name;
      });
    } else {
      // 생성모드는 값 초기화
      editMode.value = false;

      franchiseName.value = '';
      address.value = '';
      addressDetail.value = '';
      contact.value = '';
      businessNumber.value = '';
      ceo.value = '';
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.franchise-form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 300px;
}
</style>
