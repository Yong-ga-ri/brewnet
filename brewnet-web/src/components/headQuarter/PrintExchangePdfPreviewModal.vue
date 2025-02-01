<template>
  <Dialog v-model:visible="show" :draggable="false">
    <div id="export_target">
      <h1 class="mb-8">{{ title }}</h1>

      <!-- 거래명세서 출력은 사업자 정보도 함께 표시 -->
      <InvoicePdfTable
        v-if="printType === PRINT_TYPE.HQ.EXCHANGE_INVOICE"
        :franchise-code="exchangeDetail.franchiseCode"
      />
      <ExchangeDetailTable :exchange-detail="exchangeDetail" cancel-invoice />
    </div>

    <template #footer>
      <Button size="small" label="PDF 출력" @click="exportDocument" />
    </template>
  </Dialog>
</template>

<script setup>
import { computed } from 'vue';

import ExchangeDetailTable from '@/components/headQuarter/ExchangeDetailTable.vue';
import { PRINT_TYPE } from '@/utils/constant';
import PrintPDFManager from '@/utils/PrintPDFManager';

import InvoicePdfTable from './InvoicePdfTable.vue';

const { exchangeDetail, printType } = defineProps({
  exchangeDetail: {
    type: Object,
    required: true,
  },
  printType: {
    type: String,
    required: false,
    default: PRINT_TYPE.HQ.EXCHANGE_DRAFT,
  },
});
const show = defineModel('show', { type: Boolean, required: true });

const title = computed(() => {
  if (printType === PRINT_TYPE.HQ.EXCHANGE_DRAFT) return '교환 요청서';
  if (printType === PRINT_TYPE.HQ.EXCHANGE_INVOICE) return '거래취소 명세서';
  return '';
});

const printPDFManager = new PrintPDFManager();

const exportDocument = () => {
  printPDFManager.exportPDF('#export_target', title.value);
};
</script>

<style scoped>
#export_target {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}
</style>
