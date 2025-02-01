<template>
  <table class="dashboard-table">
    <slot></slot>
  </table>
</template>

<script setup></script>

<style scoped>
.dashboard-table {
  width: 100%;

  :slotted(th) {
    background-color: var(--p-surface-100);
    padding: 3px 5px;
    border: 1px solid var(--p-surface-200);
    font-size: 13px;
  }

  :slotted(td) {
    padding: 3px 5px;
    border: 1px solid var(--p-surface-200);
    text-align: center;
    font-size: 13px;
  }

  :slotted(.action-button) {
    font-size: 13px;
    padding: 3px 5px;
  }

  :slotted(.ellipsis) {
    max-width: 100px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
}
</style>
