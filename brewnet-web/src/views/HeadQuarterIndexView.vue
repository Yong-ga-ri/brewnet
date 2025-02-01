<template>
  <MainContainer>
    <HeadQuarterTopBar />
    <RouterView />

    <Toast group="notification" position="bottom-right" />
  </MainContainer>
</template>

<script setup>
import { RouterView } from 'vue-router';

import MainContainer from '@/components/common/MainContainer.vue';
import HeadQuarterTopBar from '@/components/headQuarter/HeadQuarterTopBar.vue';
</script>

<style scoped></style>
