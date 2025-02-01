<template>
  <div v-if="bigImageStore.isShow" class="big-image-modal">
    <button type="button" class="close-button" @click="bigImageStore.closeModal">
      <i class="pi pi-times"></i>
    </button>
    <img :src="showingImage" alt="이미지" class="image" />
  </div>
</template>

<script setup>
import { computed } from 'vue';

import { useBigImageStore } from '@/stores/bigImage';

const bigImageStore = useBigImageStore();

const showingImage = computed(() => {
  return bigImageStore.images.at(bigImageStore.currentPosition);
});
</script>

<style scoped>
.big-image-modal {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.3);
  width: 100dvw;
  height: 100dvh;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 4;

  .image {
    max-width: 80%;
    max-height: 80%;
    object-fit: contain;
  }

  .close-button {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 50px;
    height: 50px;
    border-radius: 5px;
    background-color: rgba(255, 255, 255, 0.8);
    outline: none;
    border: none;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-radius: 50%;
    }

    i {
      font-size: 20px;
    }
  }
}
</style>
