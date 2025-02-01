<template>
  <AppNavbar class="fc-navbar">
    <template #start>
      <RouterLink :to="{ name: 'fc:home' }">
        <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
      </RouterLink>
    </template>

    <template #end>
      <nav>
        <div class="menu-btn">
          <Button
            v-for="menu in topBarMenus"
            :key="menu.label"
            :label="menu.label"
            variant="text"
            severity="secondary"
            as="router-link"
            :to="{ name: menu.routerName }"
            active-class="topbar-link-active"
          />
        </div>

        <div>
          <Button
            type="button"
            icon="pi pi-chevron-down"
            aria-haspopup="true"
            aria-controls="overlay_menu"
            aria-label="User Menu"
            :label="`[${userStore.franchiseName}] ${userStore.username} 님`"
            severity="secondary"
            size="small"
            icon-pos="right"
            rounded
            @click="toggleUserMenu"
          />

          <Menu id="overlay_menu" ref="userMenu" :model="userMenus" :popup="true" />
        </div>

        <Button
          type="button"
          label="알림함"
          icon="pi pi-bell"
          :badge="notiCount"
          badge-severity="danger"
          size="small"
          variant="outlined"
          severity="secondary"
          rounded
          @click="openNotificationBox"
        />
      </nav>
    </template>
  </AppNavbar>

  <Drawer v-model:visible="showNotificationBox" header="알림함" position="right">
    <ul class="noti-list">
      <li v-for="(notiItem, i) in myNotiList" :key="notiItem.message" class="noti-list-item">
        <p>
          {{ notiItem.message }}
        </p>
        <div class="noti-buttons">
          <Button
            size="small"
            variant="text"
            icon="pi pi-trash"
            severity="secondary"
            label="삭제"
            @click="removeNoti(i)"
          />
          <Button
            v-if="!notiItem.isRead"
            size="small"
            label="읽음으로 표시"
            icon="pi pi-check"
            variant="text"
            @click="readNoti(i)"
          />
        </div>
      </li>
    </ul>
    <div v-if="myNotiList.length === 0" class="empty-noti">받은 알림이 없습니다.</div>
  </Drawer>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import AppMenu from '@/router/AppMenu';
import { useNotificationStore } from '@/stores/notification';
import { useUserStore } from '@/stores/user';
import AuthApi from '@/utils/api/AuthApi';

import AppNavbar from '../common/AppNavbar.vue';

const appMenu = new AppMenu();
const authApi = new AuthApi();

const userStore = useUserStore();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const notificationStore = useNotificationStore();

const userMenu = ref();
const userMenus = ref([
  // {
  //   label: '로그아웃',
  //   icon: 'pi pi-sign-out',
  //   command: clickLogout,
  // },
  {
    label: '로그아웃',
    icon: 'pi pi-sign-out',
    command: clickForceLogout,
  },
]);
const showNotificationBox = ref(false);

const topBarMenus = computed(() => {
  return appMenu.getFcTopBarMenu();
});
const notiCount = computed(() => {
  const count = notificationStore.getUnreadNotificationCount(userStore.memberCode);
  return count > 0 ? count.toString() : null;
});
const myNotiList = computed(() => {
  return notificationStore.getMemberNotifications(userStore.memberCode);
});

const toggleUserMenu = event => {
  userMenu.value.toggle(event);
};

const handleLogout = () => {
  authApi.logout().then(() => {
    userStore.clearUserData();
    router.replace({ name: 'auth:login' });
  });
};

function clickLogout() {
  showConfirm({
    header: '로그아웃',
    message: `로그아웃을 진행합니다.`,
    acceptLabel: '로그아웃',
    onAccept: handleLogout,
  });
}

function clickForceLogout() {
  userStore.clearUserData();
  router.replace({ name: 'auth:login' });
}

const openNotificationBox = () => {
  showNotificationBox.value = true;
};

const readNoti = targetIndex => {
  notificationStore.readNoti(userStore.memberCode, targetIndex);
};

const removeNoti = targetIndex => {
  notificationStore.removeNoti(userStore.memberCode, targetIndex);
};
</script>

<style scoped>
.fc-navbar {
  .logo {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: contain;
  }

  nav {
    display: flex;
    align-items: center;
    gap: 10px;

    a.topbar-link-active {
      color: var(--p-primary-600);
      border-bottom: 2px solid var(--p-primary-600);
    }
  }
}

.noti-list {
  padding: 0;
  list-style-type: none;
  font-size: 0.8rem;

  .noti-list-item {
    word-break: break-word;
    border: 1px solid #ffe066;
    border-radius: 5px;
    background-color: #fff9db;
    padding: 10px;
  }

  .noti-list-item + .noti-list-item {
    margin-top: 20px;
  }

  .noti-buttons {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 10px;
  }
}
</style>
