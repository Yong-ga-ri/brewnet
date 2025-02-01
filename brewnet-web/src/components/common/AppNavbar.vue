<template>
  <div class="app-navbar-container">
    <slot name="start"></slot>
    <slot name="end"></slot>
  </div>
</template>

<script setup></script>

<style scoped>
.app-navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  padding: 6px 20px;
  box-shadow:
    rgba(0, 0, 0, 0.1) 0px 1px 3px 0px,
    rgba(0, 0, 0, 0.06) 0px 1px 2px 0px;
  z-index: 3;
}
</style>
