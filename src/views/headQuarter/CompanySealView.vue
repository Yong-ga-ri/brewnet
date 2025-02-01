<template>
  <div class="company-seal-container">
    <img v-if="sealImageUrl" :src="sealImageUrl" alt="법인인감 이미지" class="seal" />
    <EmptyContent
      v-else-if="sealImageUrl === null"
      text="등록된 법인인감이 없습니다."
      :fallback-label="isHQAuthenticated(userStore.memberRole, ROLE.MASTER) ? '등록하기' : undefined"
      @fallback="goUpload"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import EmptyContent from '@/components/common/EmptyContent.vue';
import { useUserStore } from '@/stores/user';
import CompanyApi from '@/utils/api/CompanyApi';
import { ROLE } from '@/utils/constant';
import { isHQAuthenticated } from '@/utils/helper';

const router = useRouter();
const userStore = useUserStore();

const sealImageUrl = ref('');

const companyApi = new CompanyApi();

const getCompanySeal = () => {
  companyApi.getCompanySeal().then(data => {
    if (!data) return;

    sealImageUrl.value = data.imageUrl;
  });
};

const goUpload = () => {
  router.push({ name: 'hq:settings:company-seal:upload' });
};

onMounted(() => {
  getCompanySeal();
});
</script>

<style scoped>
.company-seal-container {
  img.seal {
    width: 250px;
    height: 250px;
  }
}
</style>
