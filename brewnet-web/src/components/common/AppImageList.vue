<template>
  <ul class="app-image-list-container" :class="{ vertical }">
    <li v-for="(image, index) in images" :key="image" @click="openModal(images, index)">
      <img :src="image" alt="이미지" />
      <div class="overlay"></div>
    </li>
  </ul>
</template>

<script setup>
import { useBigImageStore } from '@/stores/bigImage';

const { images, vertical } = defineProps({
  images: {
    type: Array,
    required: true,
  },

  vertical: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const { openModal } = useBigImageStore();
</script>

<style scoped>
.app-image-list-container {
  list-style-type: none;
  padding: 0;
  display: flex;
  gap: 10px;

  &.vertical {
    flex-direction: column;
  }

  li {
    position: relative;
  }

  img {
    object-fit: contain;
    border: 1px solid var(--p-content-border-color);
  }

  img,
  .overlay {
    width: 100px;
    height: 100px;
  }

  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    &:hover {
      background-color: rgba(0, 0, 0, 0.45);
      cursor: pointer;
    }
  }
}
</style>
