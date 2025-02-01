// 참고 블로그
// https://velog.io/@byunghun-jake/JavaScript%EB%A1%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%A6%AC%EC%82%AC%EC%9D%B4%EC%A7%95%EC%95%95%EC%B6%95-%ED%95%98%EA%B8%B0

import dayjs from 'dayjs';

export default class FileUtil {
  async compressFile(file) {
    if (file.size <= 1 * 1024 * 1024) {
      // 1MB 이하는 파일 그대로 사용
      return file;
    }

    const BLOB_TYPE = 'image/jpeg';
    const ext = 'jpeg';

    const dataUrl = await this.#readFileAsDataURL(file);
    const image = await this.#loadImage(dataUrl);
    const compressedBlob = await this.#convertImageToCompressedBlob(image, BLOB_TYPE);
    const randomString = Math.random().toString(36).substring(2); // Math.random().toString() -> 0.xxxxxxxxxx 에서 "0."를 삭제
    const compressedFile = new File(
      [compressedBlob],
      `compressed_${randomString}_${dayjs().format('YYYYMMDDHHmmssSSS')}.${ext}`,
      {
        type: compressedBlob.type,
      },
    );

    return compressedFile;
  }

  #readFileAsDataURL(file) {
    return new Promise((resolve, reject) => {
      const fileReader = new FileReader();

      fileReader.onload = () => {
        const dataUrl = fileReader.result;

        if (!dataUrl || typeof dataUrl !== 'string') {
          reject(new Error('Invalid data URL'));
          return;
        }

        resolve(dataUrl);
      };
      fileReader.onerror = reject;
      fileReader.readAsDataURL(file);
    });
  }

  #loadImage(url) {
    return new Promise((resolve, reject) => {
      const image = new Image();

      image.onload = () => resolve(image);
      image.onerror = reject;
      image.src = url;
    });
  }

  #convertImageToCompressedBlob(image, blobType) {
    const MAX_WIDTH = 1920;
    const MAX_HEIGHT = 1080;
    const BLOB_QUALITY = 0.7;

    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas');

      const ratio = Math.min(MAX_WIDTH / image.width, MAX_HEIGHT / image.height, 1);

      const newWidth = image.width * ratio;
      const newHeight = image.height * ratio;
      canvas.width = newWidth;
      canvas.height = newHeight;

      const canvasContext = canvas.getContext('2d');
      if (!canvasContext) {
        throw new Error('Cannot get canvas context');
      }

      canvasContext.drawImage(image, 0, 0, newWidth, newHeight);
      canvas.toBlob(
        blob => {
          if (!blob) {
            reject(new Error('Cannot convert canvas to Blob'));
            return;
          }

          resolve(blob);
        },
        blobType,
        BLOB_QUALITY,
      );
    });
  }
}
