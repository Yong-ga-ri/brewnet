<template>
  <div v-if="currentDeliveryDetail" class="container">
    <div class="title-area">
      <h1>{{ formatKoDraftKind(currentDeliveryDetail.deliveryKind) }} 배송</h1>
      <h3 class="sub">배송 코드: {{ currentDeliveryDetail.code }}</h3>
    </div>

    <div class="step-list">
      <div v-for="stepItem in deliveryStepList" :key="stepItem.value" class="step-item">
        <div class="left">
          <div class="v-stepper">
            <div
              class="line"
              :class="{
                active: activeStepValueList.includes(stepItem.value),
              }"
            ></div>
            <div class="dot" :class="{ active: activeStepValueList.includes(stepItem.value) }"></div>
          </div>

          <div class="content">
            <p :class="{ active: activeStepValueList.includes(stepItem.value) }" class="step-title">
              {{ stepItem.title }}
            </p>
            <small class="desc">{{ stepItem.desc }}</small>
          </div>
        </div>

        <Button
          v-if="isNextStep(stepItem.value)"
          label="배송 상태 변경"
          rounded
          size="small"
          @click="() => clickChangeDeliveryStatus(stepItem.value)"
        />
      </div>
    </div>

    <AppTableStyled>
      <thead>
        <tr>
          <th width="100">상품명</th>
          <th width="100">수량</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in currentDeliveryDetail.items" :key="item.itemName">
          <td class="align-center">{{ item.itemName }}</td>
          <td class="align-center">{{ item.quantity.toLocaleString() }}</td>
        </tr>
      </tbody>
    </AppTableStyled>
  </div>
  <div v-else class="empty-delivery-detail">
    <EmptyContent :text="errMsg" />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import EmptyContent from '@/components/common/EmptyContent.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import DeliverApi from '@/utils/api/DeliveryApi';
import {
  DRAFT_KIND,
  EXCHANGE_DELIVERY_STEP_LIST,
  EXCHANGE_STATUS,
  ORDER_DELIVERY_STEP_LIST,
  ORDER_STATUS,
  RETURN_DELIVERY_STEP_LIST,
} from '@/utils/constant';
import { formatKoDraftKind, formatKoExchangeStatus, formatKoOrderStatus, formatKoReturnStatus } from '@/utils/format';

const toast = useToast();
const { showConfirm } = useAppConfirmModal();

const currentDeliveryDetail = ref(null);
const errMsg = ref('');

const getStepTitle = stepValue => {
  if (currentDeliveryDetail.value?.deliveryKind === DRAFT_KIND.ORDER) {
    return formatKoOrderStatus(stepValue);
  }

  if (currentDeliveryDetail.value?.deliveryKind === DRAFT_KIND.EXCHANGE) {
    return formatKoExchangeStatus(stepValue);
  }

  if (currentDeliveryDetail.value?.deliveryKind === DRAFT_KIND.RETURN) {
    return formatKoReturnStatus(stepValue);
  }
};

const getStepDesc = stepValue => {
  if (stepValue === ORDER_STATUS.APPROVED) {
    return '상품을 픽업해주세요.';
  }

  if (stepValue === ORDER_STATUS.SHIPPING) {
    return '안전하게 전달해주세요.';
  }

  if (stepValue === ORDER_STATUS.SHIPPED) {
    return '배송 완료!';
  }

  if (stepValue === EXCHANGE_STATUS.PICKING) {
    return '상품을 안전하게 회수해주세요.';
  }

  if (stepValue === EXCHANGE_STATUS.PICKED) {
    return '회수 완료!';
  }
};

const makeStepItem = stepValue => {
  return { value: stepValue, title: getStepTitle(stepValue), desc: getStepDesc(stepValue) };
};

const deliveryStepList = computed(() => {
  if (!currentDeliveryDetail.value) return [];

  if (currentDeliveryDetail.value.deliveryKind === DRAFT_KIND.ORDER) {
    return ORDER_DELIVERY_STEP_LIST.map(e => makeStepItem(e));
  }

  if (currentDeliveryDetail.value.deliveryKind === DRAFT_KIND.EXCHANGE) {
    return EXCHANGE_DELIVERY_STEP_LIST.map(e => makeStepItem(e));
  }

  if (currentDeliveryDetail.value.deliveryKind === DRAFT_KIND.RETURN) {
    return RETURN_DELIVERY_STEP_LIST.map(e => makeStepItem(e));
  }

  return [];
});

// 현재 배송상태 단계의 index
const currentStepIndex = computed(() => {
  if (!currentDeliveryDetail.value) return;
  return deliveryStepList.value.findIndex(e => e.value === currentDeliveryDetail.value.deliveryStatus);
});

const activeStepValueList = computed(() => {
  if (!currentDeliveryDetail.value) return [];

  // 현재 단계를 포함하여 이전 단계들은 모두 Active 상태
  return deliveryStepList.value.slice(0, currentStepIndex.value + 1).map(e => e.value);
});

const deliveryApi = new DeliverApi();

const getDeliveryDetail = () => {
  deliveryApi
    .getCurrentDelivery()
    .then(data => {
      currentDeliveryDetail.value = data;
    })
    .catch(e => {
      errMsg.value = e.message;
      currentDeliveryDetail.value = null;
    });
};

const changeDeliveryStatus = toStatus => {
  deliveryApi
    .changeDeliveryStatus({
      code: currentDeliveryDetail.value.code,
      deliveryKind: currentDeliveryDetail.value.deliveryKind,
      deliveryStatus: toStatus,
    })
    .then(() => {
      toast.add({ severity: 'success', summary: '처리 성공', detail: '상태 변경 완료', life: 3000 });
      getDeliveryDetail();
    });
};

const clickChangeDeliveryStatus = toStatus => {
  showConfirm({
    header: '배송상태 변경',
    message: `[${getStepTitle(toStatus)}] 상태로 변경하시겠습니까?`,
    acceptLabel: '변경',
    onAccept: () => {
      changeDeliveryStatus(toStatus);
    },
  });
};

const isNextStep = targetStep => {
  const targetStepIndex = deliveryStepList.value.findIndex(e => e.value === targetStep);

  // 현재 상태가 마지막 단계라면
  if (currentStepIndex.value === deliveryStepList.value.length - 1) return false;

  // 1 차이가 난다면 targetStep이 다음 step임.
  return targetStepIndex - currentStepIndex.value === 1;
};

onMounted(() => {
  getDeliveryDetail();
});
</script>

<style scoped>
.container {
  padding: 32px 16px;
  display: flex;
  flex-direction: column;

  .title-area {
    margin-bottom: 32px;
    align-self: flex-start;

    .sub {
      margin-top: 4px;
    }
  }

  .step-list {
    list-style-type: none;
    padding: 0 20px;
    width: 100vw;
    align-self: center;
    margin-bottom: 32px;

    .step-item {
      display: flex;
      justify-content: space-between;
      align-items: center;

      & + & {
        margin-top: 20px;
      }

      .step-title {
        &.active {
          color: var(--p-primary-700);
        }
      }

      .left {
        display: flex;
        gap: 10px;

        .desc {
          color: var(--p-surface-600);
        }
      }
    }
  }

  .v-stepper {
    position: relative;
    top: 5px;

    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: var(--p-surface-300);

      &.active {
        background-color: var(--p-primary-700);
      }
    }

    .line {
      position: absolute;
      bottom: 30px;
      left: 4px;
      height: 65px;
      border-left: 2px solid var(--p-surface-300);
      z-index: -1;

      &.active {
        border-color: var(--p-primary-700);
      }
    }
  }

  .step-item:first-child .line {
    height: 0;
  }
}

.empty-delivery-detail {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
