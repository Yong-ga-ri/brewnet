<template>
  <table class="app-table-styled" :class="{ full: fullWidth }">
    <slot></slot>
  </table>
</template>

<script setup>
const { fullWidth } = defineProps({
  fullWidth: {
    type: Boolean,
    required: false,
    default: false,
  },
});
</script>

<style scoped>
.app-table-styled {
  &.full {
    width: 100%;
  }

  :slotted(th) {
    background-color: var(--p-surface-100);
    padding: 5px;
    border: 1px solid var(--p-surface-400);
    font-size: 0.9rem;
  }

  :slotted(td) {
    padding: 5px;
    border: 1px solid var(--p-surface-400);
    font-size: 0.9rem;
  }

  :slotted(td.align-center) {
    text-align: center;
  }

  :slotted(td.align-right) {
    text-align: right;
  }

  :slotted(tr.highlight) {
    background-color: #fff9db;
  }
}
</style>
