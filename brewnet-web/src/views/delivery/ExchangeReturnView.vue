<template>
  <div class="container">
    <div class="top">
      <h2 class="title">교환/반품 배송 목록</h2>
      <Button size="small" icon="pi pi-refresh" severity="secondary" rounded @click="reloadData" />
    </div>

    <DeliveryList :delivery-items="paginatedExchangeDeliveries" @change-delivery-status="changeDeliveryStatus" />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';

import DeliveryList from '@/components/delivery/DeliveryList.vue';
import DeliverApi from '@/utils/api/DeliveryApi';
import { DRAFT_KIND } from '@/utils/constant';

const toast = useToast();

const paginatedExchangeDeliveries = ref([]);
const page = ref(0);
const pageSize = ref(15);
const totalElements = ref(0);

const deliveryApi = new DeliverApi();

const changeDeliveryStatus = (code, deliveryKind, toStatus) => {
  deliveryApi
    .changeDeliveryStatus({
      code,
      deliveryKind,
      deliveryStatus: toStatus,
    })
    .then(() => {
      toast.add({ severity: 'success', summary: '처리 성공', detail: '상태 변경 완료', life: 3000 });
      reloadData();
    });
};

const getDeliveryList = () => {
  deliveryApi
    .getDeliveryList({ page: page.value, pageSize: pageSize.value, deliveryKind: DRAFT_KIND.EXCHANGE })
    .then(data => {
      paginatedExchangeDeliveries.value = data.content;
      totalElements.value = data.totalElements;
    })
    .catch(e => {
      paginatedExchangeDeliveries.value = [];
      totalElements.value = 0;
    });
};

const reloadData = () => {
  getDeliveryList();
};

onMounted(() => {
  getDeliveryList();
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  padding: 0 16px;

  .top {
    padding: 16px 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
