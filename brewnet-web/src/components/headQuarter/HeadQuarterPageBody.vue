<template>
  <div class="page-body">
    <!-- side bar -->
    <SideMenuBar />

    <main>
      <div class="app-breadcrumb">
        <i class="pi pi-home"></i>
        <template v-for="breadcrumb in breadcrumbs" :key="breadcrumb">
          <i class="pi pi-chevron-right icon-arrow"></i>
          <p>{{ breadcrumb }}</p>
        </template>
      </div>

      <PageTitle>{{ currentPageTitle }}</PageTitle>

      <div v-if="isAuthenticated" class="content">
        <RouterView />

        <Button
          v-if="$route.meta?.backTo !== undefined"
          label="돌아가기"
          severity="secondary"
          size="small"
          class="back-to-list-button"
          @click="$router.back()"
        />
      </div>
      <div v-else class="not-authenticated">접근 권한이 없습니다.</div>

      <LoadingSpinner />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { useUserStore } from '@/stores/user';
import { isHQAuthenticated } from '@/utils/helper';

import PageTitle from './PageTitle.vue';
import LoadingSpinner from '../common/LoadingSpinner.vue';
import SideMenuBar from '../common/SideMenuBar.vue';

const route = useRoute();
const userStore = useUserStore();

const currentPageTitle = computed(() => {
  return route.meta?.breadcrumb || '';
});

const breadcrumbs = computed(() => {
  const matchedRoutes = route.matched;
  return matchedRoutes.filter(e => e.meta.breadcrumb).map(e => e.meta.breadcrumb);
});

// 페이지 권한이 있는 유저인지 체크
const isAuthenticated = computed(() => {
  const pageRole = route.meta?.auth;
  if (!pageRole) return true; // 본사 누구나 접근 가능

  const currentMemberRole = userStore.memberRole;

  return isHQAuthenticated(currentMemberRole, pageRole);
});
</script>

<style scoped>
.page-body {
  flex-grow: 1;
  display: flex;
  overflow-y: auto;

  main {
    flex-grow: 1;
    overflow-y: auto;
    padding: 5px 5px 20px 5px;
    position: relative;

    .content,
    .not-authenticated {
      margin-top: 24px;
      padding: 0 20px;
    }

    .back-to-list-button {
      margin-top: 32px;
    }
  }

  .app-breadcrumb {
    display: flex;
    align-items: center;
    gap: 5px;
    padding-top: 20px;
    padding-left: 14px;
    padding-bottom: 10px;
    color: var(--p-surface-500);
    font-size: 0.85rem;
  }

  .icon-arrow {
    font-size: 0.7rem;
  }
}
</style>
