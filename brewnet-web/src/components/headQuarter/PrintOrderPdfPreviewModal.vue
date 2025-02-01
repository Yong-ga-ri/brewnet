<template>
  <Dialog v-model:visible="show" :draggable="false">
    <div id="export_target">
      <h1 class="mb-8">{{ title }}</h1>

      <!-- 거래명세서 출력은 사업자 정보도 함께 표시 -->
      <InvoicePdfTable v-if="printType === PRINT_TYPE.HQ.ORDER_INVOICE" :franchise-code="orderDetail.franchiseCode" />
      <OrderDetailTable :order-detail="orderDetail" />
    </div>

    <template #footer>
      <Button size="small" label="PDF 출력" @click="exportDocument" />
    </template>
  </Dialog>
</template>

<script setup>
import { computed } from 'vue';

import { PRINT_TYPE } from '@/utils/constant';
import PrintPDFManager from '@/utils/PrintPDFManager';

import InvoicePdfTable from './InvoicePdfTable.vue';
import OrderDetailTable from './OrderDetailTable.vue';

const { orderDetail, printType } = defineProps({
  orderDetail: {
    type: Object,
    required: true,
  },
  printType: {
    type: String,
    required: false,
    default: PRINT_TYPE.HQ.ORDER_DRAFT,
  },
});
const show = defineModel('show', { type: Boolean, required: true });

const title = computed(() => {
  if (printType === PRINT_TYPE.HQ.ORDER_DRAFT) return '주문 요청서';
  if (printType === PRINT_TYPE.HQ.ORDER_INVOICE) return '거래 명세서';
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
}
</style>
