<template>
  <aside class="side-menu">
    <nav>
      <ul v-for="menu in sideMenus" :key="menu.label" class="menu-group">
        <h5 class="group-title">{{ menu.label }}</h5>
        <li v-for="menuItem in menu.items" :key="menuItem.label">
          <RouterLink :to="{ name: menuItem.routerName }" active-class="sidebar-link-active">{{
            menuItem.label
          }}</RouterLink>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import AppMenu from '@/router/AppMenu';

const route = useRoute();
const appMenu = new AppMenu();

const sideMenus = computed(() => {
  return appMenu.getSideMenus(route.name);
});
</script>

<style scoped>
.side-menu {
  background-color: var(--p-content-background);
  width: fit-content;
  flex-shrink: 0;
  box-shadow: rgba(0, 0, 0, 0.1) 1px 0px 3px 0px;
  z-index: 2;
  padding: 20px 0;

  .menu-group {
    list-style-type: none;
    padding: 0;

    & + & {
      margin-top: 16px;
    }

    .group-title {
      padding: 10px 14px;
    }

    a {
      color: var(--p-button-text-plain-color);
      display: block;
      transition: background-color 0.2s;
      padding: 5px 14px;
      font-size: 0.9rem;

      &.sidebar-link-active {
        background-color: var(--p-primary-50);
        color: var(--p-primary-600);
      }

      &:hover {
        background-color: var(--p-button-text-plain-hover-background);
      }
    }
  }
}
</style>
