import { SORTING_OPTION } from '../constant';
import BaseApiService from './BaseApiService';

export default class HQNoticeApi extends BaseApiService {
  constructor() {
    super('/v1/hq/notice');
  }

  //
  // GET
  //

  // 공지사항 목록 조회
  getNoticeList({ page = 0, pageSize = 15, sort = SORTING_OPTION.DATE_DESC, title, writerName }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('sort', sort);
    if (title) {
      searchParams.append('title', title);
    }
    if (writerName) {
      searchParams.append('writerName', writerName);
    }

    return this.get(`?${searchParams.toString()}`);
  }

  // 공지사항 상세조회
  getNotice(noticeCode) {
    return this.get(`/${noticeCode}`);
  }
}
