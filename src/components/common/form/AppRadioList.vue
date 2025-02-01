<template>
  <AppFormField :label="label" :label-position="labelPosition">
    <div class="radio-list">
      <div v-for="option in options" :key="option.id" class="radio-item">
        <RadioButton
          :value="option.value"
          :input-id="option.inputId"
          :model-value="modelValue"
          :name="name"
          size="small"
          @change="onChange"
        />
        <AppLabel :label-for="option.inputId" :label="option.label" />
      </div>
    </div>
  </AppFormField>
</template>

<script setup>
import AppLabel from '../AppLabel.vue';
import AppFormField from './AppFormField.vue';

const { name, options, modelValue, label, labelPosition } = defineProps({
  name: {
    type: String,
    required: true,
  },
  options: {
    type: Array,
    required: true,
  },
  /**
   * options: [{
   *   value: string,
   *   label: string,
   *   inputId: string,
   * }]
   */
  modelValue: {
    type: String,
    required: true,
  },

  // required false
  label: {
    type: [String, null],
    required: false,
    default: null,
  },
  labelPosition: {
    type: String,
    required: false,
    default: 'top',
  },
});

const emit = defineEmits(['update:modelValue']);

const onChange = event => {
  emit('update:modelValue', event.target.value);
};
</script>

<style scoped>
.radio-list {
  display: flex;
  align-items: center;
  gap: 10px;

  .radio-item {
    display: flex;
    align-items: center;
    gap: 5px;
  }
}
</style>
