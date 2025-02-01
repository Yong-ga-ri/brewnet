import BaseApiService from './BaseApiService';

export default class ItemApi extends BaseApiService {
  constructor() {
    super('/v1/item');
  }

  //
  // GET
  //

  // 상품 목록 조회 (프랜차이즈용)
  getItems({ page = 0, pageSize = 15, itemUniqueCode, itemName }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);

    if (itemUniqueCode) {
      searchParams.append('itemCode', itemUniqueCode);
    }

    if (itemName) {
      searchParams.append('itemName', itemName);
    }

    return this.get(`?${searchParams.toString()}`);
  }

  // 상풍 상세 조회
  getItem(itemCode) {
    return this.get(`/${itemCode}`);
  }
}
