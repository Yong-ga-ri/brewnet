import BaseApiService from './BaseApiService';

export default class MasterNoticeApi extends BaseApiService {
  constructor() {
    super('/v1/master/notice');
  }

  //
  // POST
  //

  // 공지사항 등록
  createNotice({ title, content, imageFiles = [] }) {
    const formData = new FormData();
    const createNoticeRequestDTO = {
      title,
      content,
    };
    formData.append('createNoticeRequestDTO', this.makeBlobJson(createNoticeRequestDTO));
    imageFiles.forEach(file => {
      formData.append('image', file);
    });

    return this.post('', formData);
  }

  //
  // PUT
  //

  // 공지사항 수정
  editNotice({ noticeCode, title, content, imageFiles = [] }) {
    const formData = new FormData();
    const updateNoticeRequestDTO = {
      noticeCode,
      title,
      content,
    };
    formData.append('updateNoticeRequestDTO', this.makeBlobJson(updateNoticeRequestDTO));
    imageFiles.forEach(file => {
      formData.append('image', file);
    });

    return this.put('', formData);
  }

  //
  // DELETE
  //

  // 공지사항 삭제
  deleteNotice(noticeCode) {
    return this.delete('', { noticeCode });
  }
}
