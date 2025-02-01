import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class FCReturnApi extends BaseApiService {
  constructor() {
    super('/v1/franchise/return');
  }

  //
  // GET
  //

  // 반품신청 목록 조회
  getReturnList({ page = 0, pageSize = 15, startDate, endDate, criteria, keyword }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    return this.get(`/search?${searchParams.toString()}`);
  }

  // 반품신청 가능한 주문 목록 조회
  getAvailableReturnList() {
    return this.get('/available-orders');
  }

  // 반품할 상품 조회
  getAvailableOrderItemsList(orderCode) {
    return this.get(`/available-items/${orderCode}`);
  }

  // 반품요청 상세조회
  getReturnDetail(returnCode) {
    return this.get(`/${returnCode}`);
  }

  // 반품요청의 상태 History
  getReturnStatusHistory(returnCode) {
    return this.get(`/status/${returnCode}`);
  }

  //
  // POST
  //

  // 반품신청
  createReturn({ orderCode, returningItemCodeList, reason, explanation, sumPrice, imageFiles }) {
    const formData = new FormData();
    const dto = {
      orderCode,
      returningItemList: returningItemCodeList.map(e => ({ itemCode: e })),
      reason,
      explanation,
      sumPrice,
    };
    formData.append('returningReqVO', this.makeBlobJson(dto));

    imageFiles.forEach(imageFile => {
      formData.append('returningImage', imageFile);
    });

    return this.post('/', formData);
  }

  // 반품 취소
  cancelReturn(returnCode) {
    return this.post(`/cancel/${returnCode}`);
  }
}
