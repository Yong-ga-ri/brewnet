import { DRAFT_KIND } from '../constant';
import BaseApiService from './BaseApiService';

export default class DeliverApi extends BaseApiService {
  constructor() {
    super('/v1/delivery');
  }

  //
  // GET
  //

  // 주문 배송 목록 조회
  getDeliveryList({ page = 0, pageSize = 15, deliveryKind = DRAFT_KIND.ORDER }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('deliveryKind', deliveryKind);

    return this.get(`?${searchParams.toString()}`);
  }

  // 현재 진행중인 배송 조회
  getCurrentDelivery() {
    return this.get('/detail');
  }

  //
  // PUT
  //

  // 배송 상태 변경
  changeDeliveryStatus({ code, deliveryKind, deliveryStatus }) {
    return this.put('/status', { code, deliveryKind, deliveryStatus });
  }
}
