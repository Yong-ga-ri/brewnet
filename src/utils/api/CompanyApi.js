import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class CompanyApi extends BaseApiService {
  constructor() {
    super('/v1/company');
  }

  //
  // GET
  //

  // 회사 정보 조회
  getCompanyInfo() {
    return this.get('');
  }

  // 회사 법인인감 조회
  getCompanySeal() {
    return this.get('/seal');
  }

  // 회사 법인인감 사용내역 조회
  getCompanySealHistory({
    page = 0,
    pageSize = 15,
    sort,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).add(1, 'day').format('YYYY-MM-DD'));

    if (sort) {
      searchParams.append('sort', sort);
    }

    return this.get(`/seal/history?${searchParams.toString()}`);
  }
}
