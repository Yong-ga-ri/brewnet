<template>
  <AppFormField :label="label" :full-width="fullWidth" :label-position="labelPosition">
    <InputNumber
      :value="modelValue"
      :default-value="defaultValue"
      size="small"
      :placeholder="placeholder"
      :fluid="fullWidth"
      :readonly="readOnly"
      :disabled="disabled"
      :use-grouping="useGrouping"
      :suffix="suffix"
      :mode="krwCurrency ? 'currency' : undefined"
      :currency="krwCurrency ? 'KRW' : undefined"
      :locale="krwCurrency ? 'ko-KR' : undefined"
      @value-change="onChangeValue"
    />
  </AppFormField>
</template>

<script setup>
import AppFormField from './AppFormField.vue';

const {
  label,
  modelValue,
  placeholder,
  fullWidth,
  readOnly,
  disabled,
  labelPosition,
  useGrouping,
  suffix,
  krwCurrency,
  textAlign,
  defaultValue,
} = defineProps({
  modelValue: {
    type: [Number, null],
    required: true,
  },

  // required false
  label: {
    type: String,
    required: false,
    default: '',
  },
  placeholder: {
    type: String,
    required: false,
    default: '',
  },
  fullWidth: {
    type: Boolean,
    required: false,
    default: false,
  },
  readOnly: {
    type: Boolean,
    required: false,
    default: false,
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false,
  },
  labelPosition: {
    type: String,
    required: false,
    default: 'top',
  },
  useGrouping: {
    type: Boolean,
    required: false,
    default: true,
  },
  suffix: {
    type: [String, null],
    required: false,
    default: null,
  },
  krwCurrency: {
    type: Boolean,
    required: false,
    default: false,
  },
  textAlign: {
    type: [String, null],
    required: false,
    default: null,
  },
  defaultValue: {
    type: [Number, null],
    required: false,
    default: null,
  },
});

const emit = defineEmits(['update:modelValue']);

function onChangeValue(newVale) {
  emit('update:modelValue', newVale);
}
</script>

<style scoped></style>
