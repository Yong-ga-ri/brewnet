import { onUnmounted, ref } from 'vue';

import FileUtil from '@/utils/FileUtil';

export function useFiles() {
  const uploadFiles = ref([]);

  const fileUtil = new FileUtil();

  const onChangeFiles = async event => {
    const { files } = event.target;
    if (files.length > 0) {
      // 파일 리사이징
      const promises = Array.from(files).map(file => fileUtil.compressFile(file));
      const compressedFiles = await Promise.all(promises);

      const newFiles = compressedFiles.map(e => ({ file: e, preview: URL.createObjectURL(e) }));
      uploadFiles.value = uploadFiles.value.concat(newFiles);
    }
  };

  const onRemove = targetIndex => {
    URL.revokeObjectURL(uploadFiles.value.at(targetIndex).preview);
    uploadFiles.value = uploadFiles.value.filter((e, i) => i !== targetIndex);
  };

  onUnmounted(() => {
    // 남아있는 리소스 정리
    uploadFiles.value.forEach(e => {
      URL.revokeObjectURL(e.preview);
    });
  });

  return { uploadFiles, onChangeFiles, onRemove };
}
