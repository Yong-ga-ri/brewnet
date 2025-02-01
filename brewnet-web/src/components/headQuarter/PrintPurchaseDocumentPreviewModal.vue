<template>
  <AppModalBody action-button-label="PDF 출력" @click-action-button="exportDocument">
    <div id="export_target" class="body-area">
      <h1 class="mb-8">발주서</h1>
      <AppTableStyled>
        <tbody>
          <tr>
            <th>직인</th>
          </tr>
          <tr>
            <td><img :src="purchaseDetail.sealImageUrl" class="seal-image" /></td>
          </tr>
        </tbody>
      </AppTableStyled>

      <AppTableStyled full-width>
        <tbody>
          <tr>
            <th colspan="6">발주 번호</th>
            <th colspan="4">발주일자</th>
            <th colspan="5">발주 담당자</th>
            <th colspan="5">연락처</th>
          </tr>
          <tr>
            <td class="align-center" colspan="6">{{ purchaseDetail.letterOfPurchaseCode }}</td>
            <td class="align-center" colspan="4">{{ purchaseDetail.createdAt }}</td>
            <td class="align-center" colspan="5">{{ purchaseDetail.memberName }}</td>
            <td class="align-center" colspan="5">{{ purchaseDetail.companyContact }}</td>
          </tr>
          <tr>
            <th colspan="6">기업명</th>
            <th colspan="4">대표자명</th>
            <th colspan="5">사업자등록번호</th>
            <th colspan="5">법인등록번호</th>
          </tr>
          <tr>
            <td class="align-center" colspan="6">{{ purchaseDetail.companyName }}</td>
            <td class="align-center" colspan="4">{{ purchaseDetail.ceoName }}</td>
            <td class="align-center" colspan="5">{{ formatBusinessNumber(purchaseDetail.businessNumber) }}</td>
            <td class="align-center" colspan="5">{{ formatCorporateNumber(purchaseDetail.corporateNumber) }}</td>
          </tr>
          <tr>
            <th colspan="8">창고명</th>
            <th colspan="8">창고 주소</th>
            <th colspan="4">창고 연락처</th>
          </tr>
          <tr>
            <td class="align-center" colspan="8">{{ purchaseDetail.storageName }}</td>
            <td class="align-center" colspan="8">{{ purchaseDetail.storageAddress }}</td>
            <td class="align-center" colspan="4">{{ formatLocalTelephoneNumber(purchaseDetail.storageContact) }}</td>
          </tr>
        </tbody>
      </AppTableStyled>
      <AppTableStyled full-width>
        <tbody>
          <tr>
            <th colspan="10">거래처명</th>
            <th colspan="6">거래처 연락처</th>
            <th colspan="2">거래처 담당자</th>
          </tr>
          <tr>
            <td class="align-center" colspan="10">{{ purchaseDetail.correspondentName }}</td>
            <td class="align-center" colspan="6">{{ purchaseDetail.correspondentContact }}</td>
            <td class="align-center" colspan="2">{{ purchaseDetail.managerName }}</td>
          </tr>
        </tbody>
      </AppTableStyled>
      <AppTableStyled full-width>
        <tbody>
          <tr>
            <th colspan="2">상품코드</th>
            <th colspan="4">상품명</th>
            <th colspan="2">단가</th>
            <th colspan="3">수량</th>
            <th colspan="3">공급가</th>
            <th colspan="2">부가세</th>
          </tr>
          <tr v-for="item in purchaseDetail.items" :key="item.itemUniqueCode">
            <td class="align-center" colspan="2">{{ item.itemUniqueCode }}</td>
            <td colspan="4">{{ item.itemName }}</td>
            <td class="align-right" colspan="2">{{ item.purchasePrice.toLocaleString() }}</td>
            <td class="align-center" colspan="3">{{ item.quantity.toLocaleString() }}</td>
            <td class="align-right" colspan="3">{{ (item.quantity * item.purchasePrice).toLocaleString() }}</td>
            <td class="align-right" colspan="2">{{ (item.quantity * item.vatPrice).toLocaleString() }}</td>
          </tr>
          <tr>
            <th>합계</th>
            <td colspan="10" class="align-center">
              {{ (purchaseDetail.sumPrice + purchaseDetail.vatSum).toLocaleString() }}
            </td>
            <td class="align-right" colspan="3">{{ purchaseDetail.sumPrice.toLocaleString() }}</td>
            <td class="align-right" colspan="3">{{ purchaseDetail.vatSum.toLocaleString() }}</td>
          </tr>
        </tbody>
      </AppTableStyled>
    </div>
  </AppModalBody>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue';
import { useRoute } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import { formatBusinessNumber, formatCorporateNumber, formatLocalTelephoneNumber } from '@/utils/format';
import PrintPDFManager from '@/utils/PrintPDFManager';

import AppModalBody from '../common/AppModalBody.vue';

const route = useRoute();
const hqPurchaseApi = new HQPurchaseApi();
const { purchaseCode } = route.params;

const purchaseDetail = reactive({
  letterOfPurchaseCode: 0,
  createdAt: '',
  memberName: '',
  positionName: '',
  companyName: '',
  businessNumber: '',
  corporateNumber: '',
  ceoName: '',
  companyContact: '',
  sealImageUrl: '',
  items: [],
  sumPrice: 0,
  vatSum: 0,
  correspondentName: '',
  managerName: '',
  correspondentContact: '',
  storageName: '',
  storageAddress: '',
  storageContact: '',
});

const getPurchaseDetail = () => {
  hqPurchaseApi.printExportDocument(purchaseCode).then(data => {
    Object.assign(purchaseDetail, data);
  });
};

const printPDFManager = new PrintPDFManager();

const exportDocument = () => {
  printPDFManager.exportPDF('#export_target', '발주서');
};

onMounted(async () => {
  getPurchaseDetail();
});
</script>

<style scoped>
#export_target {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.body-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.seal-image {
  max-width: 50px;
  max-height: 50px;
}
</style>
