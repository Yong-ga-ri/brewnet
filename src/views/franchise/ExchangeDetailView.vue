<template>
  <div v-if="exchangeDetail" class="exchange-detail-container">
    <Tag
      size="small"
      :value="formatKoExchangeStatus(exchangeDetail.status)"
      :severity="getExchangeStatusSeverity(exchangeDetail.status)"
    />

    <div class="status-history">
      <div v-for="(step, i) in statusStepList" :key="i" class="status-item">
        <div class="stepper">
          <div class="dot" :class="{ active: step.active }"></div>
          <div class="line" :class="{ active: step.active }"></div>
        </div>
        <p class="title" :class="{ active: step.active }">
          {{ formatKoExchangeStatus(step.status) }}
        </p>
        <p class="date" :class="{ active: step.active }">{{ step.processedAt }}</p>
      </div>
    </div>

    <AppLabelText label="주문코드" :text="exchangeDetail.orderCode" />

    <div>
      <AppLabel use-margin-bottom label="교환 상품" />
      <AppTableStyled>
        <thead>
          <tr>
            <th>상품코드</th>
            <th>상품명</th>
            <th>수량</th>
            <th>단가</th>
            <th>주문금액</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="exchangeItem in exchangeDetail.exchangeItemList" :key="exchangeItem.itemCode">
            <td class="align-center">{{ exchangeItem.itemUniqueCode }}</td>
            <td class="align-center">{{ exchangeItem.itemName }}</td>
            <td class="align-center">{{ exchangeItem.quantity.toLocaleString() }}</td>
            <td class="align-right">{{ exchangeItem.sellingPrice.toLocaleString() }}</td>
            <td class="align-right">{{ exchangeItem.partSumPrice.toLocaleString() }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <th>금액합계</th>
            <td colspan="4" class="align-right">{{ totalPrice.toLocaleString() }}</td>
          </tr>
        </tfoot>
      </AppTableStyled>
    </div>

    <AppLabelText label="교환사유" :text="formatKoExchangeReason(exchangeDetail.reason)" />

    <AppLabelText label="교환사유 설명" :text="exchangeDetail.explanation" />

    <AppImageList :images="exchangeDetail.exchangeImageList" />

    <Button
      v-if="exchangeDetail.status === EXCHANGE_STATUS.REQUESTED"
      label="교환취소"
      severity="secondary"
      size="small"
      @click="clickCancel"
    />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import AppImageList from '@/components/common/AppImageList.vue';
import AppLabel from '@/components/common/AppLabel.vue';
import AppLabelText from '@/components/common/AppLabelText.vue';
import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import FCExchangeApi from '@/utils/api/FCExchangeApi';
import { EXCHANGE_STATUS, EXCHANGE_STEP_LIST } from '@/utils/constant';
import { formatKoExchangeReason, formatKoExchangeStatus } from '@/utils/format';
import { getExchangeStatusSeverity } from '@/utils/helper';

const route = useRoute();
const { exchangeCode } = route.params;
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const exchangeDetail = ref(null);
const statusHistory = ref([]);
const statusStepList = computed(() => {
  return EXCHANGE_STEP_LIST.reduce((acc, current) => {
    const match = statusHistory.value.find(e => e.status === current);
    acc.push({ status: current, active: match !== undefined, processedAt: match?.processedAt || null });

    return acc;
  }, []);
});
const totalPrice = computed(() => {
  if (exchangeDetail.value === null) return 0;

  return exchangeDetail.value.exchangeItemList.reduce((acc, current) => acc + current.partSumPrice, 0);
});

const fcExchangeApi = new FCExchangeApi();

const getPageData = () => {
  // 교환 상세정보
  fcExchangeApi.getExchangeDetail(exchangeCode).then(data => {
    exchangeDetail.value = data;
  });

  // 교환 상태변경 이력
  fcExchangeApi.getExchangeStatus(exchangeCode).then(data => {
    statusHistory.value = data;
  });
};

const onCancel = () => {
  fcExchangeApi.cancelExchange(exchangeCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '교환요청이 취소되었습니다.', life: 3000 });
    getPageData();
  });
};

const clickCancel = () => {
  showConfirm({
    header: '교환취소',
    message: '교환요청을 취소하시겠습니까?',
    acceptLabel: '네, 취소합니다.',
    onAccept: onCancel,
  });
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.exchange-detail-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  align-items: flex-start;

  .status-history {
    padding: 16px;
    border-radius: 5px;
    border: 1px solid var(--p-surface-300);
    box-shadow: var(--p-primary-100) 0px 2px 8px 0px;
    display: flex;
    justify-content: center;
    align-self: stretch;
  }

  .status-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 120px;

    &:last-child .line {
      display: none;
    }

    .stepper {
      position: relative;
    }

    .dot {
      background-color: var(--p-surface-400);
      width: 14px;
      height: 14px;
      border-radius: 50%;
      border: 3px solid white;
      position: relative;
      z-index: 2;

      &.active {
        background-color: var(--p-primary-700);
      }
    }

    .line {
      position: absolute;
      width: 130px;
      height: 2px;
      background-color: var(--p-surface-400);
      top: 6px;
      z-index: 1;

      &.active {
        background-color: var(--p-primary-700);
      }
    }

    .title {
      color: var(--p-surface-400);

      &.active {
        color: var(--p-primary-700);
      }
    }

    .date {
      visibility: hidden;
      color: var(--p-primary-700);
      font-size: 12px;
      text-align: center;

      &.active {
        visibility: visible;
      }
    }
  }
}
</style>
