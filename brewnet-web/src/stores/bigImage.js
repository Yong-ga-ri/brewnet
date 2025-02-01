import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useBigImageStore = defineStore('bigImage', () => {
  const images = ref([]);
  const isShow = ref(false);
  const currentPosition = ref(0);

  const closeModal = () => {
    images.value = [];
    currentPosition.value = 0;
    isShow.value = false;
  };

  const openModal = (imageList = [], initPosition = 0) => {
    images.value = imageList;
    currentPosition.value = initPosition;
    isShow.value = true;
  };

  return { images, isShow, currentPosition, closeModal, openModal };
});
