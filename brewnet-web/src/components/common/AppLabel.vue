<template>
  <label class="app-label" :for="labelFor" :class="{ mb5px: useMarginBottom }">{{ label }}</label>
</template>

<script setup>
const { labelFor, label, useMarginBottom } = defineProps({
  label: {
    type: String,
    required: true,
  },

  labelFor: {
    type: String,
    required: false,
    default: undefined,
  },

  useMarginBottom: {
    type: Boolean,
    required: false,
    default: false,
  },
});
</script>

<style scoped>
label.app-label {
  font-size: 0.9rem;
  color: var(--p-surface-600);
  display: inline-block;

  &.mb5px {
    margin-bottom: 5px;
  }
}
</style>
