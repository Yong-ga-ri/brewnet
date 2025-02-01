<template>
  <AppFormField :label="label" :full-width="fullWidth">
    <IconField>
      <InputIcon class="pi pi-lock" />
      <InputText
        :value="modelValue"
        :name="name"
        :placeholder="placeholder"
        :fluid="fullWidth"
        :type="isEyeOpen ? 'text' : 'password'"
        :size="smallSize ? 'small' : undefined"
        :style="{ textAlign }"
        @keypress="onPressPassword"
        @input="onChangeInput"
      />
      <InputIcon :class="`pi ${isEyeOpen ? 'pi-eye' : 'pi-eye-slash'}`" @click="clickEyeIcon" />
    </IconField>

    <Popover ref="popOverCapsLock" :dismissable="false">
      <p>CapsLock이 켜져있습니다.</p>
    </Popover>
  </AppFormField>
</template>

<script setup>
import { ref } from 'vue';

import AppFormField from './AppFormField.vue';

const { label, modelValue, name, placeholder, fullWidth, smallSize, textAlign } = defineProps({
  label: {
    type: String,
    required: false,
    default: '',
  },
  modelValue: {
    type: String,
    required: true,
  },
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
  smallSize: {
    type: Boolean,
    required: false,
    default: true,
  },
  textAlign: {
    type: String,
    required: false,
    default: 'left',
  },
});

const emit = defineEmits(['update:modelValue']);

const popOverCapsLock = ref();
const isEyeOpen = ref(false);

function onChangeInput(event) {
  emit('update:modelValue', event.target.value);
}

const clickEyeIcon = () => {
  isEyeOpen.value = !isEyeOpen.value;
};

// Caps Lock 상태 체크
const onPressPassword = event => {
  const isOnCapsLock = event.getModifierState('CapsLock');
  if (isOnCapsLock) {
    popOverCapsLock.value.show(event);
  } else {
    popOverCapsLock.value.hide();
  }
};
</script>

<style scoped></style>
