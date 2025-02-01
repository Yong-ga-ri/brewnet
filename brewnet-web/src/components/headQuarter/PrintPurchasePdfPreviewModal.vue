<template>
  <Dialog v-model:visible="show" :draggable="false">
    <div id="export_target" class="body-area">
      <h1 class="mb-8">구매품의서</h1>
      <template v-if="purchaseDetail">
        <DraftApprovalLine
          class="approval-line-table"
          :draft-manager-name="purchaseDetail.memberName"
          :approval-lines="purchaseApprovalLines"
        />
        <AppTableStyled full-width>
          <tbody>
            <tr>
              <th>거래처</th>
              <td>{{ purchaseDetail.correspondentName }}</td>
              <th>입고창고</th>
              <td>{{ purchaseDetail.storageName }}</td>
              <th>발주일자</th>
              <td>{{ purchaseDetail.createdAt }}</td>
              <th>담당자</th>
              <td>{{ purchaseDetail.memberName }}</td>
            </tr>
            <tr>
              <th>상품코드</th>
              <th colspan="3">상품명</th>
              <th>수량</th>
              <th>단가</th>
              <th>공급가액</th>
              <th>부가세</th>
            </tr>
            <tr v-for="item in purchaseDetail.items" :key="item.itemCode">
              <td class="align-center">{{ item.itemCode }}</td>
              <td colspan="3">{{ item.itemName }}</td>
              <td class="align-center">{{ item.quantity.toLocaleString() }}</td>
              <td class="align-right">{{ item.purchasePrice.toLocaleString() }}</td>
              <td class="align-right">{{ item.purchaseSum.toLocaleString() }}</td>
              <td class="align-right">{{ item.purchaseVat.toLocaleString() }}</td>
            </tr>
            <tr>
              <th>총 발주금액</th>
              <td colspan="5" class="align-center">{{ purchaseDetail.totalPrice.toLocaleString() }}</td>
              <td class="align-right">{{ purchaseDetail.sumPrice.toLocaleString() }}</td>
              <td class="align-right">{{ purchaseDetail.vatPrice.toLocaleString() }}</td>
            </tr>
          </tbody>
        </AppTableStyled>
      </template>
    </div>

    <template #footer>
      <Button size="small" label="PDF 출력" @click="exportDocument" />
    </template>
  </Dialog>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import DraftApprovalLine from '@/components/headQuarter/DraftApprovalLine.vue';
import { useUserStore } from '@/stores/user';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import PrintPDFManager from '@/utils/PrintPDFManager';

const route = useRoute();
const userStore = useUserStore();
const hqPurchaseApi = new HQPurchaseApi();
const purchaseApprovalLines = ref([]);
const { purchaseCode } = route.params;

const { purchaseDetail } = defineProps({
  purchaseDetail: {
    type: Object,
    required: true,
  },
});
const show = defineModel('show', { type: Boolean, required: true });

const getPurchaseApprovalLines = () => {
  hqPurchaseApi.getPurchaseApprovalLines(purchaseCode).then(data => {
    purchaseApprovalLines.value = data.approvers.map(e => ({
      ...e,
      createdAt: e.approvedAt,
    }));
  });
};

const printPDFManager = new PrintPDFManager();

const exportDocument = () => {
  printPDFManager.exportPDF('#export_target', '구매품의서');
};

onMounted(() => {
  getPurchaseApprovalLines();
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

  .approval-line-table {
    align-self: flex-end;
  }
}
</style>
