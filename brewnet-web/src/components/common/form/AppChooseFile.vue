<template>
  <AppFormField :label="label" :label-position="labelPosition">
    <Button size="small" label="파일 선택" variant="outlined" severity="secondary" @click="clickChoose" />
    <input
      ref="inputRef"
      type="file"
      accept="image/jpeg, image/png"
      style="display: none"
      @change="emit('changeFile', $event)"
    />
  </AppFormField>
</template>

<script setup>
import { ref } from 'vue';

import AppFormField from './AppFormField.vue';

const { label, labelPosition } = defineProps({
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
const emit = defineEmits(['changeFile']);

const inputRef = ref(null);

const clickChoose = () => {
  inputRef.value?.click();
};
</script>
