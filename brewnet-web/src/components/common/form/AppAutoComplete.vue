<template>
  <AppFormField :label="label" :label-position="labelPosition">
    <AutoComplete
      :model-value="modelValue"
      option-label="label"
      size="small"
      complete-on-focus
      :fluid="fullWidth"
      :suggestions="suggestions"
      :placeholder="placeholder"
      @complete="emit('completeInput', $event)"
      @item-select="itemSelected"
    >
      <template #option="slotProps">
        <div>{{ slotProps.option.label }}</div>
      </template>
      <template #footer>
        <slot name="footer"></slot>
      </template>
    </AutoComplete>
  </AppFormField>
</template>

<script setup>
import AppFormField from './AppFormField.vue';

const { label, modelValue, fullWidth, suggestions, placeholder, labelPosition } = defineProps({
  modelValue: {
    type: [Object, null],
    required: true,
  },

  suggestions: {
    type: Array,
    required: true,
  },
  /**
   * suggestions: [{
   *   code: string     // 서버와 통신하기 위한 key 값
   *   label: string    // 화면에 표시될 텍스트
   * }]
   */

  //
  // required false
  //
  label: {
    type: [String, null],
    required: false,
    default: null,
  },

  fullWidth: {
    type: Boolean,
    required: false,
    default: false,
  },

  placeholder: {
    type: String,
    required: false,
    default: undefined,
  },

  labelPosition: {
    type: String,
    required: false,
    default: 'top',
  },
});
const emit = defineEmits(['completeInput', 'update:modelValue']);

const itemSelected = e => {
  emit('update:modelValue', e.value);
};
</script>

<style scoped></style>
