<template>
  <div class="app-label-text-container">
    <AppLabel :label="label" />
    <p class="text">{{ text }}</p>
  </div>
</template>

<script setup>
import AppLabel from './AppLabel.vue';

const { label, text } = defineProps({
  label: {
    type: String,
    required: true,
  },

  text: {
    type: [String, Number],
    required: true,
  },
});
</script>

<style scoped>
.app-label-text-container {
  display: flex;
  flex-direction: column;
  gap: 5px;

  .text {
    font-size: 0.9rem;
  }
}
</style>
