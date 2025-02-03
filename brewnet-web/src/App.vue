<template>
  <RouterView />

  <AppConfirmModal />
  <Toast position="top-center" :breakpoints="toastBreakpoints" />
  <BigImageModal />
</template>

<script setup>
import { EventSourcePolyfill } from 'event-source-polyfill';
import { useToast } from 'primevue';
import { onMounted, onUnmounted, ref, watch } from 'vue';
import { RouterView } from 'vue-router';

import AppConfirmModal from './components/common/AppConfirmModal.vue';
import BigImageModal from './components/common/BigImageModal.vue';
import { useNotificationStore } from './stores/notification';
import { useUserStore } from './stores/user';
import { USER_TYPE } from './utils/constant';
import DOMEvent from './utils/domEvent';

const toast = useToast();
const userStore = useUserStore();
const notificationStore = useNotificationStore();

let eventSource = null;
let isSubscribed = ref(false);

const toastBreakpoints = {
  '1024px': {
    width: 'initial', // 1024px 이하일 때
  },
  '768px': {
    width: '50vw', // 768px 이하일 때
  },
  '480px': {
    width: '80vw', // 480px 이하일 때
  },
};

function handleApiError(customEvent) {
  toast.add({ severity: 'error', summary: '에러', detail: customEvent.detail, life: 3000 });
}

function handleApiSuccess(customEvent) {
  toast.add({ severity: 'success', summary: '처리 성공', detail: customEvent.detail, life: 3000 });
}

function handleSSE(sseEvent) {
  const { data } = sseEvent; // 서버에서 string 메시지로 보내줌
  console.dir(sseEvent);

  toast.add({ severity: 'secondary', summary: '알림', detail: data, group: 'notification' });
  notificationStore.addMessage(userStore.memberCode, data);
}

// SSE 구독 처리
function subscribeSSE(token) {
  if (userStore.userType === USER_TYPE.DELIVERY) return; // 배송기사는 알림 구독 안함
  if (isSubscribed.value) return; // 이미 구독되어 있다면 return

  console.log('구독 진행합니다.');

  const eventSourceUrl = `${import.meta.env.VITE_SERVER_URL}/api/v1/sse/subscribe`;
  eventSource = new EventSourcePolyfill(eventSourceUrl, {
    headers: {
      Authorization: token,
    },
    heartbeatTimeout: 1000 * 60 * 10, // SSE 재구독 타임아웃을 10분으로 설정
  });
  eventSource.onopen = () => {
    isSubscribed.value = true;
  };
  eventSource.onmessage = event => {
    console.dir(event);
  };

  // event name마다 어떻게 처리할건지 정의

  // 공통 구독 (배송기사 제외)
  eventSource.addEventListener('Past', handleSSE); // 지난 알림(못받은 알림)

  // 본사 구독
  if (userStore.userType === USER_TYPE.HEADQUARTERS) {
    eventSource.addEventListener('Create Notice', handleSSE); // 공지사항 작성 (가맹점은 공지사항 보는 페이지가 없어서 일단 제외)
    eventSource.addEventListener('request approval of purchase', handleSSE); // 발주 등록 시 결재요청 (결재자에게 알림)
    eventSource.addEventListener('approve purchase', handleSSE); // 발주 결재 승인 (기안자에게 알림)
    eventSource.addEventListener('reject purchase', handleSSE); // 발주 결재 반려 (기안자에게 알림)

    eventSource.addEventListener('ReturnApprovalReqEvent', handleSSE); // 반품 결재요청 (결재자에게 알림)
    eventSource.addEventListener('ExchangeApprovalReqEvent', handleSSE); // 교환 결재요청 (결재자에게 알림)
    eventSource.addEventListener('OrderApprovalReqEvent', handleSSE); // 주문결재요청 (본사 결재자에게 알림)
    eventSource.addEventListener('OrderRequestEvent', handleSSE); // 주문요청 (본사 직원들에게 알림)
  }

  // 프랜차이즈 구독
  if (userStore.userType === USER_TYPE.FRANCHISE) {
    eventSource.addEventListener('ReturnRejectionEvent', handleSSE); // 반품 거절됨 (가맹점 모든 회원에게 알림)
    eventSource.addEventListener('ReturnApprovedEvent', handleSSE); // 반품 최종완료 (가맹점 모든 회원에게 알림)

    eventSource.addEventListener('ExchangeRejectionEvent', handleSSE); // 교환 거절됨 (가맹점 모든 회원에게 알림)
    eventSource.addEventListener('ExchangeApprovedEvent', handleSSE); // 교환 최종완료 (가맹점 모든 회원에게 알림)

    eventSource.addEventListener('OrderApprovedEvent', handleSSE); // 주문승인 (가맹점 모든 회원에게 알림)
    eventSource.addEventListener('OrderDeliveryDeparted', handleSSE); // 주문배송시작 (가맹점 모든 회원에게 알림)
    eventSource.addEventListener('OrderRejectionEvent', handleSSE); // 주문반려 (가맹점 모든 회원에게 알림)
    eventSource.addEventListener('Order Delivery Completed', handleSSE); // 주문 배송 완료
  }

  eventSource.onerror = e => {};
}

function unsubscribeSSE() {
  if (!isSubscribed.value) return; // 이미 구독해제되어있다면 return

  if (eventSource) {
    eventSource.close();
    isSubscribed.value = false;
  }
}

watch(
  () => userStore.accessToken,
  newAccessToken => {
    if (newAccessToken) {
      subscribeSSE(newAccessToken);
    } else {
      unsubscribeSSE();
    }
  },
  { immediate: true },
);

watch(isSubscribed, newIsSubscribed => {
  if (newIsSubscribed) {
    console.log('SSE 구독됨');
  } else {
    console.log('SSE 구독 해제됨');
  }
});

onMounted(() => {
  DOMEvent.subscribeApiError(handleApiError);
  DOMEvent.subscribeApiSuccess(handleApiSuccess);
  // DOMEvent.subscribeExpiredToken(handleExpiredToken);
});

onUnmounted(() => {
  DOMEvent.unsubscribeApiError(handleApiError);
  DOMEvent.unsubscribeApiSuccess(handleApiSuccess);
  // DOMEvent.unsubscribeExpiredToken(handleExpiredToken);

  // 브라우저를 강제로 닫을 경우를 대비
  unsubscribeSSE();
});
</script>

<style scoped></style>
