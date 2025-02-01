import { onUnmounted, ref } from 'vue';

import FileUtil from '@/utils/FileUtil';

export function useFile() {
  const previewUrl = ref(null);
  const file = ref(null);

  const fileUtil = new FileUtil();

  const onChangeFile = async event => {
    const { files } = event.target;
    if (files.length > 0) {
      // 파일 리사이징
      const compressedFile = await fileUtil.compressFile(files[0]);

      // 기존 파일 지우고
      URL.revokeObjectURL(previewUrl);

      // 새로운 파일로 교체
      previewUrl.value = URL.createObjectURL(compressedFile);
      file.value = compressedFile;
    }
  };

  const onRemove = () => {
    previewUrl.value = null;
    file.value = null;

    URL.revokeObjectURL(previewUrl);
  };

  onUnmounted(() => {
    // 남아있는 리소스 정리
    URL.revokeObjectURL(previewUrl);
  });

  return { previewUrl, file, onChangeFile, onRemove };
}
