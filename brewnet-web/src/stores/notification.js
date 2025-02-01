import { defineStore } from 'pinia';
import { ref } from 'vue';

// 모든 회원의 알림을 관리
// 유저별로 최대 20개만 저장
export const useNotificationStore = defineStore(
  'notification',
  () => {
    const notificationBox = ref({}); // memberCode: message 배열을 저장
    const MAX_SIZE = 20;

    const getMemberNotifications = memberCode => {
      return notificationBox.value[memberCode] || [];
    };

    const addMessage = (memberCode, message) => {
      const memberNotifications = getMemberNotifications(memberCode);
      const newMessageData = { message, isRead: false };

      if (memberNotifications.length > 0) {
        // 이미 있다면 기존 배열에 추가
        if (memberNotifications.length === MAX_SIZE) {
          memberNotifications.pop();
          memberNotifications.unshift(newMessageData);
          return;
        }

        memberNotifications.unshift(newMessageData); // 앞쪽에 추가
      } else {
        // 신규 추가
        notificationBox.value[memberCode] = [newMessageData];
      }
    };

    const getUnreadNotificationCount = memberCode => {
      const memberNotifications = getMemberNotifications(memberCode);
      if (!memberNotifications) return 0;

      const unreadNotifications = memberNotifications.filter(noti => !noti.isRead);
      return unreadNotifications.length;
    };

    const readNoti = (memberCode, targetNotiIndex) => {
      const memberNotifications = getMemberNotifications(memberCode);
      const newMemberNotifications = memberNotifications.map((e, i) => {
        if (i === targetNotiIndex) {
          return { ...e, isRead: true }; // 읽음으로 변경
        }
        return e;
      });
      notificationBox.value[memberCode] = newMemberNotifications;
    };

    const removeNoti = (memberCode, targetNotiIndex) => {
      const memberNotifications = getMemberNotifications(memberCode);
      memberNotifications.splice(targetNotiIndex, 1);
    };

    return {
      notificationBox,
      addMessage,
      getUnreadNotificationCount,
      getMemberNotifications,
      readNoti,
      removeNoti,
    };
  },
  { persist: true },
);
