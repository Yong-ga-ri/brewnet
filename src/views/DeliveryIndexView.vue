<template>
  <div class="delivery-main-container">
    <header>
      <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
      <h3>브루넷배송</h3>
      <div>
        <Button
          type="button"
          icon="pi pi-chevron-down"
          aria-haspopup="true"
          aria-controls="overlay_menu"
          aria-label="User Menu"
          :label="`배송기사 님`"
          severity="secondary"
          size="small"
          icon-pos="right"
          rounded
          @click="toggleMenu"
        />

        <Menu id="overlay_menu" ref="userMenu" :model="userMenus" :popup="true" />
      </div>
    </header>
    <main>
      <RouterView />

      <LoadingSpinner />
    </main>
    <footer>
      <nav>
        <RouterLink
          v-for="navItem in navItems"
          :key="navItem.routerName"
          :to="{ name: navItem.routerName }"
          class="tab-link"
          active-class="active"
          >{{ navItem.title }}</RouterLink
        >
      </nav>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import { useUserStore } from '@/stores/user';
import AuthApi from '@/utils/api/AuthApi';

const userStore = useUserStore();
const router = useRouter();

const authApi = new AuthApi();

const navItems = ref([
  { routerName: 'd:home', title: '주문' },
  { routerName: 'd:exchange-return', title: '교환/반품' },
  { routerName: 'd:detail', title: '배송 현황' },
]);
const userMenu = ref();

const clickLogout = () => {
  authApi.logout().then(() => {
    userStore.clearUserData();
    router.replace({ name: 'auth:login' });
  });
};

const clickForceLogout = () => {
  userStore.clearUserData();
  router.replace({ name: 'auth:login' });
};

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

const toggleMenu = event => {
  userMenu.value.toggle(event);
};
</script>

<style scoped>
.delivery-main-container {
  display: flex;
  flex-direction: column;
  width: 100vw;
  height: 100dvh;

  header {
    display: grid;
    grid-template-columns: 110px 1fr 110px;
    align-items: center;
    justify-items: center;
    padding: 10px 16px;
    border-bottom: 2px solid var(--p-surface-100);

    .logo {
      width: 50px;
      height: 50px;
      justify-self: start;
    }
  }

  main {
    flex-grow: 1;
    position: relative;
  }

  footer {
    border-top: 2px solid var(--p-surface-100);

    nav {
      display: flex;

      & > * {
        flex: 1 1;
      }
    }
    .tab-link {
      text-align: center;
      color: var(--p-surface-700);
      padding: 16px 0;

      &.active {
        background-color: var(--p-primary-600);
        color: white;
        font-weight: bold;
      }
    }
  }
}
</style>
