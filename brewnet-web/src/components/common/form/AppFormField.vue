<template>
  <div
    ref="element"
    class="app-input-container"
    :class="{ width100, flexGrow1, labelTop: labelPosition === 'top', labelLeft: labelPosition === 'left' }"
  >
    <AppLabel v-if="label" :label="label" class="app-label" />
    <slot></slot>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

import AppLabel from '../AppLabel.vue';

const { label, fullWidth, labelPosition } = defineProps({
  label: {
    type: String,
    required: false,
    default: '',
  },
  fullWidth: {
    type: Boolean,
    required: false,
    default: false,
  },
  labelPosition: {
    type: String, // top: 상단, left: 왼쪽
    required: false,
    default: 'top',
  },
});

const element = ref(null);
const width100 = ref(false);
const flexGrow1 = ref(false);

onMounted(() => {
  // fullWidth가 true인 경우 처리
  if (!fullWidth) return;

  const parentElement = element.value?.parentElement;
  if (!parentElement) return;

  // 부모의 display가 flex이면 flex grow, 아니면 width로 처리
  const computedStyle = getComputedStyle(parentElement);
  const parentDisplay = computedStyle.display;
  if (parentDisplay === 'flex') {
    flexGrow1.value = true;
    width100.value = false;
  } else {
    flexGrow1.value = false;
    width100.value = true;
  }
});
</script>

<style scoped>
.app-input-container {
  display: flex;
  gap: 5px;

  &.flexGrow1 {
    flex-grow: 1;
  }

  &.width100 {
    width: 100%;
  }

  &.labelTop {
    flex-direction: column;
  }

  &.labelLeft {
    align-items: center;

    .app-label {
      width: 70px;
    }
  }

  .app-label {
    flex-shrink: 0;
  }
}
</style>
