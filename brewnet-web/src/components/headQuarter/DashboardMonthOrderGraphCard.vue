<template>
  <Card class="dashboard-calendar-card">
    <template #title>
      <div class="top">
        <h4>월별 주문 통계</h4>

        <div class="month-navigation">
          <Button size="small" icon="pi pi-chevron-left" severity="secondary" rounded @click="prevMonth" />
          <h5>{{ currentYearMonth }}</h5>
          <Button size="small" icon="pi pi-chevron-right" severity="secondary" rounded @click="nextMonth" />
        </div>
      </div>
    </template>
    <template #content>
      <div class="line-chart-container">
        <Line
          :options="{
            responsive: true,
            // maintainAspectRatio: false, // 가로 세로 비율 유지 해제(false)
          }"
          :data="{
            datasets: [
              {
                label: '주문 금액',
                data: orderTotals,
                borderColor: 'rgb(75, 192, 192)',
                backgroundColor: 'rgb(75, 192, 192)',
              },
            ],
            labels: chartLabels,
          }"
        />
      </div>

      <div class="line-chart-container">
        <Line
          :options="{
            responsive: true,
            // maintainAspectRatio: false, // 가로 세로 비율 유지 해제(false)
          }"
          :data="{
            datasets: [
              {
                label: '주문 수',
                data: orderCounts,
                backgroundColor: 'rgb(255, 99, 132)',
                borderColor: 'rgb(255, 99, 132)',
              },
            ],
            labels: chartLabels,
          }"
        />
      </div>
    </template>
  </Card>
</template>

<script setup>
import dayjs from 'dayjs';
import { computed, ref, watch } from 'vue';
import { Line } from 'vue-chartjs';

import HQStatisticsApi from '@/utils/api/HQStatisticsApi';

const current = ref(new Date());
const orderCounts = ref([]);
const orderTotals = ref([]);

const currentYearMonth = computed(() => {
  return dayjs(current.value).format('YYYY년 M월');
});
const daysInCurrentMonth = computed(() => {
  const currentYear = current.value.getFullYear();
  const currentMonth = current.value.getMonth();
  const date = new Date(currentYear, currentMonth, 1); // 시작일
  const days = []; // 현재 월의 전체 date 리스트를 담을 공간

  while (date.getMonth() === currentMonth) {
    days.push(new Date(date));
    date.setDate(date.getDate() + 1);
  }

  return days;
});
const chartLabels = computed(() => {
  return daysInCurrentMonth.value.map(date => dayjs(date).format('M/D'));
});

const hqStatisticsApi = new HQStatisticsApi();

const prevMonth = () => {
  current.value = dayjs(current.value).subtract(1, 'month').toDate();
};

const nextMonth = () => {
  current.value = dayjs(current.value).add(1, 'month').toDate();
};

watch(
  current,
  newCurrent => {
    const currentYear = newCurrent.getFullYear();
    const currentMonth = newCurrent.getMonth();

    hqStatisticsApi
      .getOrderCalendarData(currentYear, currentMonth + 1)
      .then(foundOrderData => {
        const orderCount = [];
        const orderTotal = [];

        daysInCurrentMonth.value.forEach(date => {
          const matched = foundOrderData.find(e => e.date === dayjs(date).format('YYYY-MM-DD'));
          if (!matched) {
            orderCount.push(0);
            orderTotal.push(0);
          } else {
            orderCount.push(matched.orderCount);
            orderTotal.push(matched.orderPrice);
          }
        });

        orderCounts.value = orderCount;
        orderTotals.value = orderTotal;
      })
      .catch(e => {
        // 통계 데이터 없을 때
        orderCounts.value = Array.from({ length: daysInCurrentMonth.value.length }, () => 0);
        orderTotals.value = Array.from({ length: daysInCurrentMonth.value.length }, () => 0);
      });
  },
  { immediate: true },
);
</script>

<style scoped>
.dashboard-calendar-card {
  box-shadow: var(--p-primary-100) 0px 2px 8px 0px;

  .top {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .month-navigation {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .line-chart-container {
    position: relative;
    max-height: 450px;
  }
}
</style>
