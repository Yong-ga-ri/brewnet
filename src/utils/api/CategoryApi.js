import BaseApiService from './BaseApiService';

export default class CategoryApi extends BaseApiService {
  constructor() {
    super('/v1/category');
  }

  //
  // GET
  //

  // 하위 카테고리 목록 조회 (상위 카테고리 정보 포함)
  getCategories() {
    return this.get('');
  }

  // 상위 카테고리 목록 조회
  getSuperCategories() {
    return this.get('/super');
  }

  //
  // POST
  //

  // 카테고리 생성
  createCategory(superCategoryCode, categoryName) {
    return this.post('', {
      superCategoryCode,
      categoryName,
    });
  }

  //
  // PUT
  //

  // 상위 카테고리 수정
  editSuperCategory(categoryCode, newName) {
    return this.put('/super', {
      superCategoryCode: categoryCode,
      superCategoryName: newName,
    });
  }

  // 하위 카테고리 수정
  editSubCategory(categoryCode, newName) {
    return this.put('/sub', {
      subCategoryCode: categoryCode,
      subCategoryName: newName,
    });
  }

  //
  // DELETE
  //

  // 상위 카테고리 삭제
  removeSuperCategory(categoryCode) {
    return this.delete('/super', {
      superCategoryCode: categoryCode,
    });
  }

  // 하위 카테고리 삭제
  removeSubCategory(categoryCode) {
    return this.delete('/sub', {
      subCategoryCode: categoryCode,
    });
  }
}
