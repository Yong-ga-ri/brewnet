<template>
  <div class="company-info-container">
    <div class="button-area">
      <template v-if="isHQAuthenticated(userStore.memberRole, ROLE.MASTER)">
        <template v-if="!editMode">
          <Button v-if="companyCode" size="small" label="수정" @click="clickEdit" />
          <Button v-else size="small" label="등록" @click="clickEdit" />
        </template>
        <template v-else>
          <Button size="small" label="취소" variant="outlined" severity="secondary" @click="clickCancel" />
          <Button size="small" label="저장" @click="clickSave" />
        </template>
      </template>
    </div>

    <AppTableStyled full-width>
      <tbody>
        <tr>
          <th>상호명</th>
          <td v-if="!editMode">{{ companyInfo.name }}</td>
          <td v-else>
            <AppInputText v-model="name" />
          </td>
          <th>사업자등록번호</th>
          <td v-if="!editMode">{{ formatBusinessNumber(companyInfo.businessNum) }}</td>
          <td v-else><AppInputText v-model="businessNum" /></td>
          <th>법인등록번호</th>
          <td v-if="!editMode">{{ formatCorporateNumber(companyInfo.corporateNum) }}</td>
          <td v-else>
            <AppInputText v-model="corporateNum" />
          </td>
        </tr>
        <tr>
          <th>대표자명</th>
          <td v-if="!editMode">{{ companyInfo.ceo }}</td>
          <td v-else>
            <AppInputText v-model="ceo" />
          </td>
          <th>주소</th>
          <td v-if="!editMode" colspan="3">{{ companyInfo.address }}</td>
          <td v-else colspan="3">
            <AppInputText v-model="address" />
          </td>
        </tr>
        <tr>
          <th>업종/업태</th>
          <td v-if="!editMode">{{ companyInfo.businessType }}</td>
          <td v-else>
            <AppInputText v-model="businessType" />
          </td>
          <th>대표번호</th>
          <td v-if="!editMode">{{ companyInfo.contact }}</td>
          <td v-else>
            <AppInputText v-model="contact" />
          </td>
          <th>설립일</th>
          <td v-if="!editMode">{{ companyInfo.establishDate }}</td>
          <td v-else>
            <AppInputText v-model="establishDate" />
          </td>
        </tr>
      </tbody>
    </AppTableStyled>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref, watch } from 'vue';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import { useUserStore } from '@/stores/user';
import CompanyApi from '@/utils/api/CompanyApi';
import MasterCompanyApi from '@/utils/api/MasterCompanyApi';
import { ROLE } from '@/utils/constant';
import { formatBusinessNumber, formatCorporateNumber } from '@/utils/format';
import { isHQAuthenticated } from '@/utils/helper';

const toast = useToast();
const userStore = useUserStore();

const companyCode = ref(null);
const companyInfo = ref({
  name: '',
  businessNum: '',
  corporateNum: '',
  ceo: '',
  address: '',
  businessType: '',
  contact: '',
  establishDate: '',
});

const name = ref('');
const businessNum = ref('');
const corporateNum = ref('');
const ceo = ref('');
const address = ref('');
const businessType = ref('');
const contact = ref('');
const establishDate = ref('');
const editMode = ref(false);

const companyApi = new CompanyApi();
const masterCompanyApi = new MasterCompanyApi();

const clickEdit = () => {
  editMode.value = true;
};

const clickCancel = () => {
  editMode.value = false;
};

const clickSave = async () => {
  if (companyCode.value === null) {
    // 생성
    await masterCompanyApi.createCompanyInfo({
      name: name.value,
      businessNumber: businessNum.value,
      corporateNumber: corporateNum.value,
      ceoName: ceo.value,
      address: address.value,
      typeOfBusiness: businessType.value,
      contact: contact.value,
      dateOfEstablishment: establishDate.value,
    });
  } else {
    // 수정
    await masterCompanyApi.editCompanyInfo({
      name: name.value,
      businessNumber: businessNum.value,
      corporateNumber: corporateNum.value,
      ceoName: ceo.value,
      address: address.value,
      typeOfBusiness: businessType.value,
      contact: contact.value,
      dateOfEstablishment: establishDate.value,
    });
  }

  editMode.value = false;
  toast.add({ severity: 'success', summary: '처리 성공', detail: '회사정보가 변경되었습니다.', life: 3000 });

  // 데이터 새로고침
  getCompanyInfo();
};

const getCompanyInfo = () => {
  companyApi.getCompanyInfo().then(data => {
    if (!data) return;

    companyInfo.value = {
      name: data.name,
      businessNum: data.businessNumber,
      corporateNum: data.corporateNumber,
      ceo: data.ceoName,
      address: data.address,
      businessType: data.typeOfBusiness,
      contact: data.contact,
      establishDate: data.dateOfEstablishment,
    };

    companyCode.value = data.companyCode;
  });
};

onMounted(() => {
  getCompanyInfo();
});

watch(editMode, newVal => {
  if (newVal) {
    if (companyCode.value) {
      // 수정모드라면
      name.value = companyInfo.value.name;
      businessNum.value = companyInfo.value.businessNum;
      corporateNum.value = companyInfo.value.corporateNum;
      ceo.value = companyInfo.value.ceo;
      address.value = companyInfo.value.address;
      businessType.value = companyInfo.value.businessType;
      contact.value = companyInfo.value.contact;
      establishDate.value = companyInfo.value.establishDate;
    } else {
      // 생성모드
    }
  }
});
</script>

<style scoped>
.company-info-container {
  .button-area {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  table {
    margin-top: 20px;
  }
}
</style>
