<template>
  <div class="modal-body-container">
    <!-- content -->
    <slot></slot>

    <!-- footer -->
    <div v-if="actionButtonLabel" class="btn-area">
      <Button
        :label="actionButtonLabel"
        size="small"
        :disabled="disabledActionButton"
        @click="emit('clickActionButton')"
      />
    </div>
  </div>
</template>

<script setup>
const { actionButtonLabel, disabledActionButton } = defineProps({
  actionButtonLabel: {
    type: String,
    required: false,
    default: '',
  },
  disabledActionButton: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const emit = defineEmits(['clickActionButton']);
</script>

<style scoped>
.modal-body-container {
  .btn-area {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
