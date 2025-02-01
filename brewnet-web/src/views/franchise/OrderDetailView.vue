<template>
  <div class="order-detail-container">
    <template v-if="orderDetail">
      <div class="top-area">
        <Tag
          rounded
          :value="formatKoOrderStatus(orderDetail.orderStatus)"
          :severity="getOrderStatusSeverity(orderDetail.orderStatus)"
          class="mb-1"
        />
        <div class="top-buttons">
          <Button label="주문요청서 출력" variant="outlined" size="small" @click="clickPrintOrder" />
          <Button label="교환" variant="outlined" size="small" :disabled="!isCompleted" @click="clickExchange" />
          <Button label="반품" variant="outlined" size="small" :disabled="!isCompleted" @click="clickReturn" />
          <Button
            label="취소"
            severity="danger"
            size="small"
            variant="outlined"
            :disabled="!isRequested"
            @click="clickCancel"
          />
        </div>
      </div>

      <div class="body-area">
        <h1>주문상세</h1>

        <OrderDetailTable :order-detail="orderDetail" />
      </div>

      <PrintOrderPdfPreviewModal v-model:show="showPrintPdf" :order-detail="orderDetail" />
    </template>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OrderDetailTable from '@/components/franchise/OrderDetailTable.vue';
import PrintOrderPdfPreviewModal from '@/components/franchise/PrintOrderPdfPreviewModal.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import FCOrderApi from '@/utils/api/FCOrderApi';
import { ORDER_STATUS } from '@/utils/constant';
import { formatKoOrderStatus } from '@/utils/format';
import { getOrderStatusSeverity } from '@/utils/helper';

const route = useRoute();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const orderDetail = ref(null);
const showPrintPdf = ref(false);

const isRequested = computed(() => {
  return orderDetail.value.orderStatus === ORDER_STATUS.REQUESTED;
});

const isCompleted = computed(() => {
  return orderDetail.value.orderStatus === ORDER_STATUS.SHIPPED;
});

const fcOrderApi = new FCOrderApi();
const { orderCode } = route.params;

const clickExchange = () => {
  // 교환요청 페이지로 이동
  router.push({ name: 'fc:home:exchange:form', query: { orderCode } });
};

const clickReturn = () => {
  // 반품요청 페이지로 이동
  router.push({ name: 'fc:home:return:form', query: { orderCode } });
};

const cancelOrder = () => {
  fcOrderApi.deleteOrder(orderCode).then(() => {
    toast.add({ severity: 'error', summary: '처리 성공', detail: '주문이 취소되었습니다.', life: 3000 });
    router.replace({ name: 'fc:home:order:list' });
  });
};

const clickCancel = () => {
  showConfirm({
    header: '주문 취소',
    message: '주문을 취소하시겠습니까?',
    acceptLabel: '주문 취소',
    onAccept: cancelOrder,
    danger: true,
  });
};

const clickPrintOrder = () => {
  showPrintPdf.value = true;
};

onMounted(() => {
  // 주문 상세 데이터 셋팅
  fcOrderApi.getOrderDetail(orderCode).then(data => {
    orderDetail.value = data;
  });
});
</script>

<style scoped>
.order-detail-container {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .top-area {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .top-buttons {
      display: flex;
      gap: 5px;
    }
  }

  .body-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }
}
</style>
