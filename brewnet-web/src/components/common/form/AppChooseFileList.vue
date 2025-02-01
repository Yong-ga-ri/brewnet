<template>
  <div class="upload-image-area">
    <button type="button" class="upload-button" @click="clickChoose">
      <i class="pi pi-images"></i>
      <p>사진 선택</p>
    </button>
    <input
      ref="inputFileMultipleRef"
      type="file"
      accept="image/jpeg, image/png"
      multiple
      style="display: none"
      @change="emit('changeFiles', $event)"
    />

    <ul v-if="uploadImages.length > 0" class="upload-image-list">
      <li
        v-for="(uploadImage, i) in uploadImages"
        :key="uploadImage.preview"
        :style="{ backgroundImage: `url(${uploadImage.preview})` }"
        class="upload-image-item"
      >
        <Button
          icon="pi pi-times"
          size="small"
          rounded
          severity="secondary"
          class="remove-button"
          @click="emit('removeImage', i)"
        />
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const { uploadImages } = defineProps({
  uploadImages: {
    type: Array,
    required: true,
  },
});
const emit = defineEmits(['changeFiles', 'removeImage']);
const inputFileMultipleRef = ref();

const clickChoose = () => {
  inputFileMultipleRef.value?.click();
};
</script>

<style scoped>
.upload-image-area {
  display: flex;
  gap: 16px;

  .upload-button,
  .upload-image-item {
    width: 100px;
    height: 100px;
    border-radius: 5px;
  }

  .upload-button {
    background-color: var(--p-surface-100);
    border: none;
    outline: none;
    display: flex;
    flex-direction: column;
    gap: 5px;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background-color: var(--p-surface-200);
    }
  }

  .upload-image-list {
    list-style-type: none;
    padding: 0;
    display: flex;
    gap: 20px;

    .upload-image-item {
      background-repeat: no-repeat;
      background-position: center center;
      background-size: contain;
      border: 1px solid var(--p-content-border-color);
      position: relative;

      .remove-button {
        position: absolute;
        top: -16px;
        right: -16px;
      }
    }
  }
}
</style>
