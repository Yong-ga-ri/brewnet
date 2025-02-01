import BaseApiService from './BaseApiService';

export default class FCItemApi extends BaseApiService {
  constructor() {
    super('/v1/franchise/items');
  }

  //
  // GET
  //

  // 필수구매상품 조회
  getMustBuyItems() {
    return this.get('/must-buy');
  }
}
