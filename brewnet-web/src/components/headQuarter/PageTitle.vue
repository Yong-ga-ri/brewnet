<template>
  <h3 class="page-title">
    <slot></slot>
  </h3>
</template>

<script setup></script>

<style scoped>
.page-title {
  margin: 0 14px;
  padding-left: 10px;
  border-left: 5px solid var(--p-primary-600);
}
</style>
