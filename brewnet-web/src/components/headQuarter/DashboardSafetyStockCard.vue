<template>
  <Card class="safety-stock-card">
    <template #title>
      <div class="safety-stock-card-title">
        <h4>안전재고 위험 목록 ({{ totalElements }}건)</h4>
        <Button
          label="재고대시보드 이동"
          as="router-link"
          :to="{ name: 'hq:stock:storage-stock' }"
          size="small"
          severity="secondary"
        />
      </div>
    </template>
    <template #content>
      <DashboardTable>
        <thead>
          <tr>
            <th>상품코드</th>
            <th>상품명</th>
            <th>가용재고</th>
            <th>안전재고값</th>
            <th>가용재고 - 안전재고값</th>
            <th>미승인주문</th>
            <th>최소발주필요</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedWarningSafetyStockList.length === 0">
            <td colspan="8">{{ errMsg }}</td>
          </tr>
          <template v-else>
            <tr v-for="warningSafetyStock in paginatedWarningSafetyStockList" :key="warningSafetyStock.itemCode">
              <td>{{ warningSafetyStock.itemCode }}</td>
              <td class="ellipsis">{{ warningSafetyStock.itemName }}</td>
              <td>{{ warningSafetyStock.availableStock.toLocaleString() }}</td>
              <td>{{ warningSafetyStock.safeStock.toLocaleString() }}</td>
              <td>{{ warningSafetyStock.availableMinusSafeStock.toLocaleString() }}</td>
              <td>{{ warningSafetyStock.unApprovedOrderCount.toLocaleString() }}</td>
              <td>{{ warningSafetyStock.minPurchaseCount.toLocaleString() }}</td>
              <td>
                <Button
                  label="발주요청"
                  size="small"
                  variant="text"
                  class="action-button"
                  as="router-link"
                  :to="{
                    name: 'hq:purchase:form',
                  }"
                />
              </td>
            </tr>
          </template>
        </tbody>
      </DashboardTable>
    </template>
  </Card>
</template>

<script setup>
import { onMounted, ref } from 'vue';

import HQStatisticsApi from '@/utils/api/HQStatisticsApi';

import DashboardTable from './DashboardTable.vue';

const paginatedWarningSafetyStockList = ref([]);
const totalElements = ref(0);
const errMsg = ref('');

const hqStatisticsApi = new HQStatisticsApi();

onMounted(() => {
  hqStatisticsApi
    .getWarningSafetyStock()
    .then(data => {
      paginatedWarningSafetyStockList.value = data.content;
      totalElements.value = data.totalElements;
    })
    .catch(e => {
      errMsg.value = e.message;
    });
});
</script>

<style scoped>
.safety-stock-card {
  box-shadow: var(--p-primary-100) 0px 2px 8px 0px;

  .safety-stock-card-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
