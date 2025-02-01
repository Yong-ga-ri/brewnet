<template>
  <div class="hq-home-container">
    <PageTitle class="no-margin">대시보드</PageTitle>
    <div class="top"></div>

    <DashboardChartCard />

    <div class="bottom">
      <div class="left">
        <DashboardMonthOrderGraphCard />
      </div>

      <div class="right">
        <DashboardMyApprovalWaitCard />
        <DashboardNewOrderCard />
        <DashboardSafetyStockCard />
      </div>
    </div>

    <LoadingSpinner />
  </div>
</template>

<script setup>
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import DashboardChartCard from '@/components/headQuarter/DashboardChartCard.vue';
import DashboardMonthOrderGraphCard from '@/components/headQuarter/DashboardMonthOrderGraphCard.vue';
import DashboardMyApprovalWaitCard from '@/components/headQuarter/DashboardMyApprovalWaitCard.vue';
import DashboardNewOrderCard from '@/components/headQuarter/DashboardNewOrderCard.vue';
import DashboardSafetyStockCard from '@/components/headQuarter/DashboardSafetyStockCard.vue';
import PageTitle from '@/components/headQuarter/PageTitle.vue';
</script>

<style scoped>
.hq-home-container {
  padding: 32px 24px;
  overflow-y: auto;

  .top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 24px;
  }

  .bottom {
    display: flex;
    gap: 16px;
    margin-top: 20px;

    .left {
      flex: 1 1;
    }

    .right {
      flex: 1 1;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
  }

  .no-margin {
    margin: 0;
  }
}
</style>
