<template>
  <AppFormField :label="label" :label-position="labelPosition">
    <div class="app-date-range-picker-wrapper">
      <DatePicker
        :model-value="start"
        :max-date="end"
        show-icon
        icon-display="input"
        show-button-bar
        size="small"
        @update:model-value="onChangeStart"
      />
      <i class="pi pi-arrow-right"></i>
      <DatePicker
        :model-value="end"
        :min-date="start"
        show-icon
        icon-display="input"
        show-button-bar
        size="small"
        @update:model-value="onChangeEnd"
      />
    </div>
  </AppFormField>
</template>

<script setup>
import AppFormField from './AppFormField.vue';

const { label, labelPosition } = defineProps({
  label: {
    type: String,
    required: false,
    default: '',
  },

  labelPosition: {
    type: String,
    required: false,
    default: 'top',
  },
});

const start = defineModel('start', { type: Date, required: true });
const end = defineModel('end', { type: Date, required: true });

const onChangeStart = newValue => {
  start.value = newValue;
};

const onChangeEnd = newValue => {
  end.value = newValue;
};
</script>

<style scoped>
.app-date-range-picker-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;

  .p-datepicker {
    width: 135px;
  }
}
</style>
