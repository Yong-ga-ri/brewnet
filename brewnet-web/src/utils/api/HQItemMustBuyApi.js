import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class HQItemMustBuyApi extends BaseApiService {
  constructor() {
    super('/v1/hq/items/must-buy');
  }

  //
  // GET
  //

  // 필수 구매 상품 조회
  getMustBuyItems() {
    return this.get('');
  }

  //
  // POST
  //

  // 필수 구매 상품 지정
  setMustBuy({ itemCode, quantity, dueDate }) {
    return this.post(`/set/${itemCode}`, { quantity, dueDate: dayjs(dueDate).format('YYYY-MM-DD HH:mm:ss') });
  }

  //
  // PATCH
  //

  // 필수 구매 상품 수정
  editMustBuy({ itemCode, quantity, dueDate }) {
    return this.patch(`/update/${itemCode}`, { quantity, dueDate: dayjs(dueDate).format('YYYY-MM-DD HH:mm:ss') });
  }
}
