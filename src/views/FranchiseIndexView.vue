<template>
  <MainContainer>
    <FranchiseTopBar />
    <RouterView />

    <Toast group="notification" position="bottom-right" />
  </MainContainer>
</template>

<script setup>
import { RouterView } from 'vue-router';

import MainContainer from '@/components/common/MainContainer.vue';
import FranchiseTopBar from '@/components/franchise/FranchiseTopBar.vue';
</script>

<style scoped></style>
