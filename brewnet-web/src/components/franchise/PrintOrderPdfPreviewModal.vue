<template>
  <Dialog v-model:visible="show" :draggable="false">
    <div id="export_target">
      <h1 class="mb-8">주문 요청서</h1>
      <OrderDetailTable :order-detail="orderDetail" />
    </div>

    <template #footer>
      <Button size="small" label="PDF 출력" @click="exportDocument" />
    </template>
  </Dialog>
</template>

<script setup>
import PrintPDFManager from '@/utils/PrintPDFManager';

import OrderDetailTable from './OrderDetailTable.vue';

const { orderDetail } = defineProps({
  orderDetail: {
    type: Object,
    required: true,
  },
});
const show = defineModel('show', { type: Boolean, required: true });

const printPDFManager = new PrintPDFManager();

const exportDocument = () => {
  printPDFManager.exportPDF('#export_target', '주문 요청서');
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
