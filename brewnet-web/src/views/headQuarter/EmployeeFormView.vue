<template>
  <form class="employee-form-container" @submit.prevent="onFormSubmit">
    <!-- 아이디 -->
    <AppInputText v-if="!editMode" v-model="loginId" label="아이디" />
    <AppLabelText v-else label="아이디" :text="loginId" />

    <!-- 비밀번호 -->
    <div v-if="!editMode">
      <AppInputPassword v-model="password" label="비밀번호" class="mb-2" full-width />
      <AppInputPassword v-model="confirmPassword" full-width />
    </div>

    <!-- 성명 -->
    <AppInputText v-model="username" label="성명" />

    <!-- 이메일 -->
    <AppInputText v-model="email" label="이메일" />

    <!-- 휴대폰번호 -->
    <AppInputText v-model="phone" label="휴대폰번호" placeholder="-제외 숫자만 입력" />

    <!-- 직위 -->
    <AppSelect v-model="position" label="직위" :options="positionOptions" :initial-value="initialPosition" />

    <Button type="submit" variant="outlined" label="저장" />
  </form>
</template>

<script setup>
import { useToast } from 'primevue/usetoast';
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppLabelText from '@/components/common/AppLabelText.vue';
import AppInputPassword from '@/components/common/form/AppInputPassword.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import AuthApi from '@/utils/api/AuthApi';
import HQMemberApi from '@/utils/api/HQMemberApi';
import MemberApi from '@/utils/api/MemberApi';
import { POSITIONS } from '@/utils/constant';
import { formatKoEmployeePosition } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';
import { emailRegex, loginIdRegex, notNumber, passwordRegex } from '@/utils/regex';

const route = useRoute();
const { memberCode } = route.params;
const toast = useToast();
const router = useRouter();

const loginId = ref('');
const password = ref('');
const confirmPassword = ref('');
const username = ref('');
const email = ref('');
const phone = ref('');
const position = ref('');
const initialPosition = ref('');
const editMode = ref(false);

const positionOptions = computed(() => {
  return POSITIONS.map(e => makeSelectOption(formatKoEmployeePosition(e), e));
});

const authApi = new AuthApi();
const memberApi = new MemberApi();
const hqMemberApi = new HQMemberApi();

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

    if (!username.value) throw new Error('성명을 입력해주세요.');

    if (!email.value) throw new Error('이메일을 입력해주세요.');
    if (!emailRegex.test(email.value)) throw new Error('이메일 형식으로 입력해주세요.');

    if (!phone.value) throw new Error('휴대폰번호를 입력해주세요.');

    if (!position.value) throw new Error('직위를 선택해주세요');

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
    positionName: position.value,
  };

  if (editMode.value) {
    // 수정
    requestBody.memberCode = memberCode;
    await memberApi.changeMemberInfo(requestBody);
    successMsg = '임직원 정보가 수정되었습니다.';
  } else {
    // 생성
    requestBody.id = loginId.value;
    requestBody.password = password.value;
    await authApi.createMember(requestBody);
    successMsg = '임직원이 등록되었습니다.';
  }

  if (!successMsg) return;

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.push({ name: 'hq:settings:employee:list' });
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
        position.value = member.positionName;
        initialPosition.value = member.positionName; // Select의 default 값은 따로 설정 필요
      });
    } else {
      // 생성모드는 값 초기화
      editMode.value = false;

      loginId.value = '';
      username.value = '';
      email.value = '';
      phone.value = '';
      position.value = '';
      initialPosition.value = '';
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.employee-form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 350px;
}
</style>
