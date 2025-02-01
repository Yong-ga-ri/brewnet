<template>
  <AppTableStyled v-if="companyInfo && franchiseInfo" full-width class="mb-5 invoice-table">
    <tr>
      <th rowspan="3" class="fc type">공 급 받 는 자</th>
      <th class="fc">상호명</th>
      <td class="fc align-center">{{ franchiseInfo.franchiseName }}</td>
      <th rowspan="3" class="hq type">공 급 자</th>
      <th class="hq">상호명</th>
      <td class="hq align-center">{{ companyInfo.name }}</td>
    </tr>
    <tr>
      <th class="fc">사업자번호</th>
      <td class="fc align-center">{{ formatBusinessNumber(franchiseInfo.businessNumber) }}</td>
      <th class="hq">사업자번호</th>
      <td class="hq align-center">{{ formatBusinessNumber(companyInfo.businessNumber) }}</td>
    </tr>
    <tr>
      <th class="fc">대표자명</th>
      <td class="fc align-center">{{ franchiseInfo.name }}</td>
      <th class="hq">대표자명</th>
      <td class="hq align-center">{{ companyInfo.ceoName }}</td>
    </tr>
  </AppTableStyled>
</template>

<script setup>
import { onMounted, ref } from 'vue';

import CompanyApi from '@/utils/api/CompanyApi';
import HQFranchiseApi from '@/utils/api/HQFranchiseApi';
import { formatBusinessNumber } from '@/utils/format';

import AppTableStyled from '../common/AppTableStyled.vue';

const { franchiseCode } = defineProps({
  franchiseCode: {
    type: Number,
    required: true,
  },
});

const companyInfo = ref(null);
const franchiseInfo = ref(null);

const companyApi = new CompanyApi();
const hqFranchiseApi = new HQFranchiseApi();

onMounted(() => {
  // 회사정보 가져오기
  companyApi.getCompanyInfo().then(data => {
    companyInfo.value = data;
  });

  // 가맹점 정보 가져오기
  hqFranchiseApi.getFranchise(franchiseCode).then(data => {
    franchiseInfo.value = data;
  });
});
</script>

<style scoped>
.invoice-table {
  .hq {
    background-color: #f4fce3;
  }

  .fc {
    background-color: #fff5f5;
  }

  th.type {
    writing-mode: vertical-lr;
  }
}
</style>
