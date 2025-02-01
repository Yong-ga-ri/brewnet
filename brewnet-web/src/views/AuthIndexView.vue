<template>
  <main class="auth-main">
    <div class="wrapper">
      <!-- 상단: 브랜드 이름 고정 -->
      <div class="brand">
        <img src="@/assets/images/logo_no_bg.png" alt="" />
        <p class="desc">카페 물류 주문관리 시스템</p>
      </div>

      <!-- 하단: 화면 View -->
      <RouterView />
    </div>

    <LoadingSpinner />
  </main>
</template>

<script setup>
import { RouterView } from 'vue-router';

import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
</script>

<style scoped>
main.auth-main {
  height: 100dvh;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  .wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 70%;

    .brand {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 40px;

      img {
        margin-bottom: 10px;
        width: 150px;
      }

      .desc {
        text-align: center;
        color: var(--p-inputtext-placeholder-color);
      }
    }
  }
}
</style>
