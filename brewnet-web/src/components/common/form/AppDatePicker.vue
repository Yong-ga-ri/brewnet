<template>
  <AppFormField :label="label" :label-position="labelPosition">
    <DatePicker
      :model-value="date"
      show-icon
      icon-display="input"
      show-button-bar
      size="small"
      @update:model-value="onChangeStart"
    />
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

const date = defineModel('date', { type: [Date, null], required: true });

const onChangeStart = newValue => {
  date.value = newValue;
};
</script>

<style scoped></style>
