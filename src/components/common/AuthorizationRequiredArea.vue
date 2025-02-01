<template>
  <div v-if="modelValue === null || modelValue === undefined">허가가 필요한 구역입니다.</div>
  <form v-else-if="modelValue === false" class="authorization-required-area-false" @submit.prevent="onSubmit">
    <div class="wrapper">
      <h3 class="mb-1">본인확인</h3>
      <p class="mb-5">본인 확인을 위해 현재 비밀번호를 입력해주세요.</p>

      <AppInputPassword v-model="password" placeholder="현재 비밀번호 입력" class="mb-2" full-width />
      <Button type="submit" label="확인" size="small" :disabled="!password" fluid />
    </div>
  </form>
  <template v-else>
    <!-- 허가된 경우 -->
    <slot></slot>
  </template>
</template>

<script setup>
import { ref } from 'vue';

import MemberApi from '@/utils/api/MemberApi';

import AppInputPassword from './form/AppInputPassword.vue';

const { modelValue } = defineProps({
  modelValue: {
    type: [Boolean, null],
    required: true,
    default: null,
  },
});

const emit = defineEmits(['update:modelValue', 'authenticated']);

const password = ref('');

const memberApi = new MemberApi();

const onSubmit = () => {
  memberApi
    .checkAuthByPassword(password.value)
    .then(uuid => {
      // 통과
      emit('update:modelValue', true);
      emit('authenticated', uuid);
    })
    .catch(e => {
      // 실패
    });
};
</script>

<style scoped>
.authorization-required-area-false {
  border-radius: 5px;
  border: 1px solid var(--p-content-border-color);
  padding: 32px 16px;
  display: flex;
  justify-content: center;

  .wrapper {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    width: 500px;
  }
}
</style>
