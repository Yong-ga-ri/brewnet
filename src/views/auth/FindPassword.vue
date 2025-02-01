<template>
  <form class="find-password-container" @submit.prevent="onSubmit">
    <h2>비밀번호 찾기</h2>

    <div>
      <AppInputText v-model="id" label="아이디" class="mb-2" />
      <div class="email-area mb-2">
        <AppInputText v-model="email" label="이메일" full-width />
        <Button
          :label="isSend ? '재전송' : '인증번호 전송'"
          variant="outlined"
          severity="secondary"
          size="small"
          @click="clickSendVerifyCode"
        />
      </div>
      <AppInputText v-model="verifyCode" label="인증번호 입력" />
    </div>

    <div class="bottom-button-area">
      <Button label="돌아가기" variant="text" @click="goBack" />
      <Button label="확인" type="submit" />
    </div>
  </form>
</template>

<script setup>
import { useToast } from 'primevue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import AppInputText from '@/components/common/form/AppInputText.vue';
import { useTempUserStore } from '@/stores/tempUser';
import EmailApi from '@/utils/api/EmailApi';

const router = useRouter();
const toast = useToast();
const tempUserStore = useTempUserStore();

const id = ref('');
const email = ref('');
const verifyCode = ref('');
const isSend = ref(false);

const emailApi = new EmailApi();

const goBack = () => {
  router.back();
};

const checkFormSendCode = () => {
  try {
    if (!id.value) throw new Error('아이디를 입력해주세요.');
    if (!email.value) throw new Error('이메일을 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickSendVerifyCode = () => {
  const isPass = checkFormSendCode();
  if (!isPass) return;

  emailApi.sendVerifyCodeByEmail(id.value, email.value).then(() => {
    toast.add({
      severity: 'success',
      summary: '처리 성공',
      detail: '입력하신 이메일로 인증코드가 전송되었습니다.',
      life: 3000,
    });
    isSend.value = true;
  });
};

const checkForm = () => {
  try {
    if (!isSend.value) throw new Error('인증번호를 먼저 전송해주세요.');
    if (!verifyCode.value) throw new Error('인증번호를 입력해주세요');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const onSubmit = () => {
  const isPass = checkForm();
  if (!isPass) return;

  emailApi.checkVerifyCodeByEmail(email.value, verifyCode.value).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '인증되었습니다.', life: 3000 });
    tempUserStore.setResetPasswordLoginId(id.value); // 저장하여 다음 페이지에 사용
    router.replace({ name: 'auth:reset-password' });
  });
};
</script>

<style scoped>
.find-password-container {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .email-area {
    display: flex;
    align-items: flex-end;
    gap: 10px;

    button {
      flex-shrink: 0;
    }
  }

  .bottom-button-area {
    display: grid;
    grid-template-columns: 1fr 2.5fr;
    gap: 20px;
  }
}
</style>
