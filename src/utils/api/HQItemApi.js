import BaseApiService from './BaseApiService';

export default class HQItemApi extends BaseApiService {
  constructor() {
    super('/v1/hq/item');
  }

  //
  // GET
  //

  // 상품 목록 조회 (본사용)
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

  //
  // POST
  //

  // 상품 생성
  createItem({
    categoryCode,
    name,
    purchasePrice,
    sellingPrice,
    safetyStock,
    itemUniqueCode,
    correspondentCode,
    imageFile,
  }) {
    const formData = new FormData();
    const dto = {
      subCategoryCode: categoryCode,
      name,
      purchasePrice,
      sellingPrice,
      safetyStock,
      itemUniqueCode,
      correspondentCode,
    };
    formData.append('createItemRequestDTO', this.makeBlobJson(dto));

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.post('', formData);
  }

  //
  // PUT
  //

  // 상품 정보 수정
  editItem({
    itemCode,
    categoryCode,
    name,
    purchasePrice,
    sellingPrice,
    imageUrl, // 기존 이미지
    safetyStock,
    itemUniqueCode,
    correspondentCode,
    active, // 활성화 여부
    imageFile,
  }) {
    const formData = new FormData();
    const dto = {
      itemCode,
      subCategoryCode: categoryCode,
      name,
      purchasePrice,
      sellingPrice,
      imageUrl,
      safetyStock,
      itemUniqueCode,
      correspondentCode,
      active,
    };

    formData.append('updateItemRequestDTO', this.makeBlobJson(dto));

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.put('', formData);
  }

  //
  // DELETE
  //

  // 상품 비활성화
  deactivateItem(itemCode) {
    return this.delete('', { itemCode });
  }
}
