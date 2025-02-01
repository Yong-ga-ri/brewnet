<template>
  <ul class="delivery-list">
    <li v-for="deliveryItem in deliveryItems" :key="deliveryItem.code" class="delivery-list-item">
      <h3 class="mb-2">{{ formatKoDraftKind(deliveryItem.deliveryKind) }}코드: {{ deliveryItem.code }}</h3>
      <div class="location mb-5">
        <i class="pi pi-map-marker"></i>
        <p>
          {{ deliveryItem.deliveryFranchiseName }}
        </p>
      </div>
      <div class="buttons">
        <Button
          size="small"
          severity="secondary"
          icon="pi pi-phone"
          label="가맹점 연락"
          @click="clickCall(deliveryItem)"
        />
        <Button
          size="small"
          :label="getButtonLabel(deliveryItem)"
          :variant="getButtonVariant(deliveryItem)"
          :disabled="getButtonDisabled(deliveryItem)"
          @click="clickChangeStatus(deliveryItem)"
        />
      </div>
    </li>
  </ul>
</template>

<script setup>
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { DELIVERY_STATUS, DRAFT_KIND, EXCHANGE_STATUS, RETURN_STATUS } from '@/utils/constant';
import {
  formatKoDeliveryStatus,
  formatKoDraftKind,
  formatKoExchangeStatus,
  formatKoReturnStatus,
} from '@/utils/format';

const { deliveryItems } = defineProps({
  deliveryItems: {
    type: Array,
    required: true,
  },
});
const emit = defineEmits(['changeDeliveryStatus']);

const { showConfirm } = useAppConfirmModal();

const clickCall = data => {
  location.href = `tel:${data.contact}`;
};

const isShowActionButton = (deliveryKind, deliveryStatus) => {
  // 교환/반품 -> APPROVED 상태일 때에만
  if (deliveryKind === DRAFT_KIND.EXCHANGE) {
    return deliveryStatus === EXCHANGE_STATUS.APPROVED || deliveryStatus === EXCHANGE_STATUS.PICKED;
  }

  return deliveryStatus === RETURN_STATUS.APPROVED;
};

const getDeliveryButtonLabel = status => {
  if (status === EXCHANGE_STATUS.APPROVED) {
    return '회수 시작';
  }
  if (status === EXCHANGE_STATUS.PICKING) {
    return '회수 완료';
  }
  if (status === EXCHANGE_STATUS.PICKED) {
    return '재배송 시작';
  }
  if (status === EXCHANGE_STATUS.SHIPPING) {
    return '재배송 완료';
  }
  return '';
};

const getButtonLabel = data => {
  if (data.deliveryKind === DRAFT_KIND.ORDER) {
    return formatKoDeliveryStatus(data.deliveryStatus);
  }

  if (isShowActionButton(data.deliveryKind, data.deliveryStatus)) {
    return getDeliveryButtonLabel(data.deliveryStatus);
  }

  // 나머지 상태는 현재 상태 그대로 보여주기
  if (data.deliveryKind === DRAFT_KIND.EXCHANGE) {
    return formatKoExchangeStatus(data.deliveryStatus);
  }

  return formatKoReturnStatus(data.deliveryStatus);
};

const getButtonVariant = data => {
  if (data.deliveryKind === DRAFT_KIND.ORDER) return undefined;
  if (isShowActionButton(data.deliveryKind, data.deliveryStatus)) {
    return undefined;
  }
  return 'text';
};

const getButtonDisabled = data => {
  if (data.deliveryKind === DRAFT_KIND.ORDER) return false;
  return !isShowActionButton(data.deliveryKind, data.deliveryStatus);
};

const clickChangeStatus = data => {
  const currentStatus = data.deliveryStatus;

  // 변경할 배송상태 설정
  let header = '';
  let acceptLabel = '';
  let message = '';
  let toStatus = null;

  if (data.deliveryKind === DRAFT_KIND.ORDER) {
    // 주문일 때
    if (currentStatus === DELIVERY_STATUS.START_DELIVERY) {
      header = '배송 시작';
      acceptLabel = '배송 시작';
      message = '배송을 시작하시겠습니까?';
      toStatus = DELIVERY_STATUS.SHIPPING;
    }
  } else if (data.deliveryKind === DRAFT_KIND.EXCHANGE) {
    // 교환일 때
    if (currentStatus === EXCHANGE_STATUS.APPROVED) {
      header = '회수 시작';
      acceptLabel = '회수 시작';
      message = '회수를 시작하시겠습니까?';
      toStatus = EXCHANGE_STATUS.PICKING;
    } else if (currentStatus === EXCHANGE_STATUS.PICKED) {
      header = '재배송 시작';
      acceptLabel = '재배송 시작';
      message = '재배송을 시작하시겠습니까?';
      toStatus = EXCHANGE_STATUS.SHIPPING;
    }
  } else if (data.deliveryKind === DRAFT_KIND.RETURN) {
    // 반품일 때
    if (currentStatus === RETURN_STATUS.APPROVED) {
      header = '회수 시작';
      acceptLabel = '회수 시작';
      message = '회수를 시작하시겠습니까?';
      toStatus = RETURN_STATUS.PICKING;
    }
  }

  if (!toStatus) {
    toast.add({
      severity: 'error',
      summary: '처리 실패',
      detail: `변경할 상태값이 설정되지 않았습니다. -- ${data.deliveryKind}`,
      life: 3000,
    });
    return;
  }

  showConfirm({
    header,
    message: `[${formatKoDraftKind(data.deliveryKind)}코드: ${data.code} / 배송지: ${data.deliveryFranchiseName}]\n${message}`,
    acceptLabel,
    onAccept: () => {
      emit('changeDeliveryStatus', data.code, data.deliveryKind, toStatus);
    },
  });
};
</script>

<style scoped>
.delivery-list {
  padding: 0;
  list-style-type: none;

  .delivery-list-item {
    border: 1px solid var(--p-surface-300);
    border-radius: 5px;
    padding: 10px;
    background-color: white;
    box-shadow: var(--p-surface-200) 0px 2px 8px 0px;
  }

  .delivery-list-item + .delivery-list-item {
    margin-top: 16px;
  }

  .location {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .buttons {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
}
</style>
