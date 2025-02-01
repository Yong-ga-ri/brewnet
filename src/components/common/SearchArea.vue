<template>
  <form class="search-area-form" @submit.prevent="onSubmit" @reset.prevent="onReset">
    <div
      class="search-area"
      :class="{ grid }"
      :style="{
        gridTemplateColumns: `repeat(${gridRepeatCount}, ${gridRepeatWidth})`,
      }"
    >
      <slot></slot>
    </div>

    <div class="form-buttons">
      <Button type="reset" label="조건 초기화" icon="pi pi-filter-slash" size="small" variant="outlined" />
      <Button type="submit" label="조회" icon="pi pi-search" size="small" fluid />
    </div>
  </form>
</template>

<script setup>
const { grid, gridRepeatCount, gridRepeatWidth } = defineProps({
  grid: {
    type: Boolean,
    required: false,
    default: false,
  },
  gridRepeatCount: {
    type: [Number, null],
    required: false,
    default: 6,
  },
  gridRepeatWidth: {
    type: String,
    required: false,
    default: '1fr',
  },
});
const emit = defineEmits(['search', 'form-reset']);

const onSubmit = () => {
  emit('search');
};

const onReset = () => {
  emit('form-reset');
};
</script>

<style scoped>
.search-area-form {
  padding: 16px;
  border-radius: 5px;
  border: 1px solid var(--p-surface-300);
  margin-bottom: 32px;
  box-shadow: var(--p-primary-100) 0px 2px 8px 0px;

  .search-area {
    margin-bottom: 20px;

    &.grid {
      display: grid;
      gap: 16px;
    }
  }

  .form-buttons {
    display: flex;
    gap: 16px;

    button[type='reset'] {
      flex-shrink: 0;
    }
  }
}
</style>
