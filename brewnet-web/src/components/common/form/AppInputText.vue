<template>
  <AppFormField :label="label" :full-width="fullWidth" :label-position="labelPosition">
    <IconField v-if="icon">
      <InputIcon v-if="iconPosition === 'start'" :class="icon" />
      <InputText
        :value="numberMode ? (modelValue ? Number(modelValue).toLocaleString() : null) : modelValue"
        size="small"
        :name="name"
        :placeholder="placeholder"
        :fluid="fullWidth"
        :readonly="readOnly"
        :disabled="disabled"
        :pt="{
          root: {
            style: {
              'text-align': textAlign || 'initial',
            },
          },
        }"
        @input="onChangeInput"
        @click="onClickInput"
      />
      <InputIcon v-if="iconPosition === 'end'" :class="icon" />
    </IconField>
    <template v-else>
      <InputText
        :value="numberMode ? (modelValue ? Number(modelValue).toLocaleString() : null) : modelValue"
        size="small"
        :name="name"
        :placeholder="placeholder"
        :fluid="fullWidth"
        :readonly="readOnly"
        :disabled="disabled"
        :pt="{
          root: {
            style: {
              'text-align': textAlign || 'initial',
            },
            inputtext: {
              placeholder: {
                color: 'white',
              },
            },
          },
        }"
        @input="onChangeInput"
      />
    </template>
  </AppFormField>
</template>

<script setup>
import { notNumber } from '@/utils/regex';

import AppFormField from './AppFormField.vue';

const {
  label,
  modelValue,
  name,
  placeholder,
  fullWidth,
  readOnly,
  icon,
  iconPosition,
  disabled,
  numberMode,
  textAlign,
  labelPosition,
} = defineProps({
  label: {
    type: String,
    required: false,
    default: '',
  },
  modelValue: {
    type: [String, null],
    required: true,
  },

  // required false
  name: {
    type: [String, null],
    required: false,
    default: null,
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
  icon: {
    type: [String, null],
    required: false,
    default: null,
  },
  iconPosition: {
    type: String, // start, end
    required: false,
    default: 'start',
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false,
  },
  numberMode: {
    type: Boolean,
    required: false,
    default: false,
  },
  textAlign: {
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

const emit = defineEmits(['update:modelValue', 'onClick']);

function onChangeInput(event) {
  let newVal = event.target.value;
  if (numberMode) {
    newVal = newVal.replace(notNumber, '');
  }
  emit('update:modelValue', newVal);
}

function onClickInput() {
  emit('onClick');
}
</script>

<style scoped>
.p-inputtext {
  &::placeholder {
    color: var(--p-surface-400);
  }
}
</style>
