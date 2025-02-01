<template>
  <Tabs :value="modelValue" @update:value="onUpdate">
    <TabList>
      <Tab v-for="tabItem in tabItems" :key="tabItem.value" :value="tabItem.value">{{ tabItem.label }}</Tab>
    </TabList>
  </Tabs>
</template>

<script setup>
const { tabItems, modelValue } = defineProps({
  tabItems: {
    type: Array, // [{ label: string, value: string }]
    required: true,
  },

  modelValue: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const onUpdate = value => {
  emit('update:modelValue', value);
};
</script>

<style scoped></style>
