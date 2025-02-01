<template>
  <form class="franchise-account-form-container" @submit.prevent="onFormSubmit">
    <!-- 아이디 -->
    <AppInputText v-if="!editMode" v-model="loginId" label="아이디" name="loginId" />
    <AppLabelText v-else label="아이디" :text="loginId" />

    <!-- 비밀번호 -->
    <div v-if="!editMode">
      <AppInputPassword v-model="password" label="비밀번호" name="password" class="mb-2" />
      <AppInputPassword v-model="confirmPassword" name="confirmPassword" />
    </div>

    <!-- 점주명 -->
    <AppInputText v-model="username" label="점주명" />

    <!-- 이메일 -->
    <AppInputText v-model="email" label="이메일" name="email" />

    <!-- 휴대폰번호 -->
    <AppInputText v-model="phone" label="휴대폰번호" name="phone" />

    <!-- 담당 가맹점 -->
    <AppAutoComplete
      v-model="selectedFranchise"
      label="가맹점"
      :suggestions="franchiseSuggestions"
      full-width
      @complete-input="search"
    >
      <template #footer>
        <Button
          label="가맹점 신규 등록"
          fluid
          severity="secondary"
          text
          size="small"
          icon="pi pi-plus"
          @click="clickAddFranchise"
        />
      </template>
    </AppAutoComplete>

    <Button type="submit" variant="outlined" label="저장" />
  </form>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppLabelText from '@/components/common/AppLabelText.vue';
import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppInputPassword from '@/components/common/form/AppInputPassword.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AuthApi from '@/utils/api/AuthApi';
import HQFranchiseApi from '@/utils/api/HQFranchiseApi';
import HQMemberApi from '@/utils/api/HQMemberApi';
import MemberApi from '@/utils/api/MemberApi';
import { makeAutocompleteSuggestion } from '@/utils/helper';
import { emailRegex, loginIdRegex, notNumber } from '@/utils/regex';

const router = useRouter();
const route = useRoute();
const { memberCode } = route.params;
const toast = useToast();

const loginId = ref('');
const password = ref('');
const confirmPassword = ref('');
const username = ref('');
const email = ref('');
const phone = ref('');
const selectedFranchise = ref(null);
const filteredFranchises = ref([]);
const editMode = ref(false);

const franchiseSuggestions = computed(() => {
  return filteredFranchises.value.map(e => makeAutocompleteSuggestion(e.franchiseCode, e.franchiseName));
});

const authApi = new AuthApi();
const hqFranchiseApi = new HQFranchiseApi();
const hqMemberApi = new HQMemberApi();
const memberApi = new MemberApi();

const checkForm = () => {
  emailRegex.lastIndex = 0;
  loginIdRegex.lastIndex = 0;

  try {
    if (!editMode.value) {
      if (!loginId.value) throw new Error('아이디를 입력해주세요');
      if (!loginIdRegex.test(loginId.value)) throw new Error('아이디는 영문, 숫자만 가능합니다.');

      if (!password.value) throw new Error('비밀번호를 입력해주세요.');

      if (!confirmPassword.value) throw new Error('비밀번호를 재입력해주세요.');
      if (confirmPassword.value !== password.value) throw new Error('비밀번호가 일치하지 않습니다.');
    }

    if (!email.value) throw new Error('이메일을 입력해주세요.');
    if (!emailRegex.test(email.value)) throw new Error('이메일 형식으로 입력해주세요.');

    if (!phone.value) throw new Error('휴대폰번호를 입력해주세요.');

    if (!selectedFranchise.value) throw new Error('가맹점을 선택해주세요');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });

    return false;
  }
};

const onFormSubmit = async () => {
  const isPass = checkForm();
  if (!isPass) return;

  let successMsg = '';
  const requestBody = {
    name: username.value,
    email: email.value,
    contact: phone.value.replace(notNumber, ''),
    franchiseCode: selectedFranchise.value.code,
  };

  if (editMode.value) {
    // 수정
    requestBody.memberCode = memberCode;
    await memberApi.changeMemberInfo(requestBody);

    successMsg = '가맹점 계정 정보가 수정되었습니다.';
  } else {
    // 등록
    requestBody.id = loginId.value;
    requestBody.password = password.value;
    await authApi.createMember(requestBody);

    successMsg = '가맹점 계정이 생성되었습니다.';
  }

  if (!successMsg) return;

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.push({ name: 'hq:partner:franchise-account:list' });
};

const search = event => {
  // 가맹점 검색
  hqFranchiseApi
    .getFranchiseList({ franchiseName: event.query || undefined })
    .then(data => {
      filteredFranchises.value = data.content;
    })
    .catch(() => {
      filteredFranchises.value = [];
    });
};

const clickAddFranchise = () => {
  const fullPath = router.resolve({ name: 'hq:partner:franchise:create' }).href;
  window.open(fullPath, '_blank');
};

watch(
  () => route.params.memberCode,
  newVal => {
    // 수정모드인 경우 기본 값 설정
    if (newVal) {
      editMode.value = true;

      hqMemberApi.getMemberInfo(memberCode).then(member => {
        if (!member) return;

        loginId.value = member.id;
        username.value = member.name;
        email.value = member.email;
        phone.value = member.contact;
        selectedFranchise.value = { label: member.franchiseName, code: member.franchiseCode };
      });
    } else {
      // 생성모드는 값 초기화
      editMode.value = false;

      loginId.value = '';
      email.value = '';
      phone.value = '';
      selectedFranchise.value = null;
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.franchise-account-form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: fit-content;
}
</style>
