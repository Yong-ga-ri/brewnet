import BaseApiService from './BaseApiService';

export default class SuperOrderApi extends BaseApiService {
  constructor() {
    super('/v1/super/orders');
  }

  //
  // POST
  //

  // 주문 결재 승인
  approveOrder({ orderCode, comment }) {
    return this.post(`/approve/${orderCode}`, { comment });
  }

  // 주문 결재 반려
  rejectOrder({ orderCode, comment }) {
    return this.post(`/reject/${orderCode}`, { comment });
  }
}
