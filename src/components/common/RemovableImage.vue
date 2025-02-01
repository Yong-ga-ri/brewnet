<template>
  <div class="removable-image">
    <img :src="imageUrl" alt="이미지" />
    <button type="button" class="remove-button" @click="clickRemove">
      <i class="pi pi-times"></i>
    </button>
  </div>
</template>

<script setup>
const { imageUrl } = defineProps({
  imageUrl: {
    type: String,
    required: true,
  },
});
const emit = defineEmits(['removeImage']);

const clickRemove = () => {
  emit('removeImage', imageUrl);
};
</script>

<style scoped>
.removable-image {
  position: relative;
  width: fit-content;
  border: 1px solid var(--p-surface-300);

  img {
    width: 200px;
    height: 200px;
    object-fit: contain;
  }

  .remove-button {
    position: absolute;
    top: 4px;
    right: 4px;
    border: none;
    background-color: rgba(0, 0, 0, 0.4);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    cursor: pointer;

    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s;

    i {
      color: white;
      font-weight: bold;
    }

    &:hover {
      background-color: rgba(0, 0, 0, 0.6);
    }
  }
}
</style>
