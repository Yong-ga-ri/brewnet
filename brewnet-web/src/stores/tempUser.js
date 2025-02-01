import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTempUserStore = defineStore('tempUser', () => {
  const resetPasswordLoginId = ref('');

  const setResetPasswordLoginId = loginId => {
    resetPasswordLoginId.value = loginId;
  };

  return {
    resetPasswordLoginId,
    setResetPasswordLoginId,
  };
});
