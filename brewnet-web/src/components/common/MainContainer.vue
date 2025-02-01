<template>
  <div class="main-container">
    <slot></slot>
  </div>
</template>

<script setup></script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  overflow: hidden;
}
</style>
