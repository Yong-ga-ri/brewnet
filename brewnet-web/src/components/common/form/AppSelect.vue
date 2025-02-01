<template>
  <AppFormField :label="label" :label-position="labelPosition" :full-width="fullWidth">
    <Select
      :options="options"
      :value="modelValue"
      :default-value="initialValue"
      option-label="label"
      option-value="value"
      :option-group-label="grouped ? 'label' : undefined"
      :option-group-children="grouped ? 'items' : undefined"
      size="small"
      checkmark
      placeholder="===선택해주세요==="
      :highlight-on-select="false"
      :name="name"
      class="app-select"
      :class="{ full: fullWidth }"
      :show-clear="showClear"
      @change="onSelectChange"
    >
      <template v-if="grouped" #optiongroup="slotProps">
        <div class="group-label">{{ slotProps.option.label }}</div>
      </template>
    </Select>
    <div v-if="helperText" class="app-select-helper-text">{{ helperText }}</div>
  </AppFormField>
</template>

<script setup>
import AppFormField from './AppFormField.vue';

const { modelValue, options, initialValue, label, name, labelPosition, fullWidth, helperText, showClear, grouped } =
  defineProps({
    // 부모로부터 현재 선택된 값을 받아옴
    modelValue: {
      type: [String, Number, null],
      required: true,
    },

    options: {
      type: Array,
      required: true,
    },

    /**
     * options: [{
     *   label: string
     *   value: string
     * }]
     */

    //
    // required false
    //
    initialValue: {
      type: [String, Number],
      required: false,
      default: '',
    },

    label: {
      type: String,
      required: false,
      default: '',
    },

    name: {
      type: [String, null],
      required: false,
      default: null,
    },

    labelPosition: {
      type: String,
      required: false,
      default: 'top',
    },

    fullWidth: {
      type: Boolean,
      required: false,
      default: false,
    },

    helperText: {
      type: [String, null],
      required: false,
      default: () => null,
    },

    showClear: {
      type: Boolean,
      required: false,
      default: false,
    },

    grouped: {
      type: Boolean,
      required: false,
      default: false,
    },
  });

const emit = defineEmits(['update:modelValue']);

// 선택이 변경될 때 상위 컴포넌트로 새로운 값 전달
function onSelectChange(event) {
  emit('update:modelValue', event.value);
}
</script>

<style scoped>
.app-select.full {
  flex-grow: 1;
}
.app-select-helper-text {
  color: var(--p-surface-500);
  font-size: 12px;
}

.group-label {
  font-size: 14px;
}
</style>
