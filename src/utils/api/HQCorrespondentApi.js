import BaseApiService from './BaseApiService';

export default class HQCorrespondentApi extends BaseApiService {
  constructor() {
    super('/v1/hq/correspondent');
  }

  //
  // GET
  //

  // 거래처 목록 조회
  getCorrespondents({ page = 1, pageSize = 15, correspondentCode, correspondentName }) {
    const searchParams = new URLSearchParams();
    searchParams.append('pageNumber', page);
    searchParams.append('pageSize', pageSize);

    if (correspondentCode) {
      searchParams.append('correspondentCode', correspondentCode);
    }

    if (correspondentName) {
      searchParams.append('correspondentName', correspondentName);
    }

    return this.get(`?${searchParams.toString()}`);
  }

  // 특정 거래처가 취급하는 상품 조회
  getCorrespondentItems({ page = 1, pageSize = 15, correspondentCode, itemUniqueCode, itemName }) {
    const searchParams = new URLSearchParams();
    searchParams.append('pageNumber', page);
    searchParams.append('pageSize', pageSize);
    searchParams.append('correspondentCode', correspondentCode);

    if (itemUniqueCode) {
      searchParams.append('itemUniqueCode', itemUniqueCode);
    }

    if (itemName) {
      searchParams.append('itemName', itemName);
    }

    return this.get(`/items?${searchParams.toString()}`);
  }

  // 엑셀 다운로드용 전체 데이터
  getAllCorrespondentList({ correspondentCode, correspondentName }) {
    const searchParams = new URLSearchParams();

    if (correspondentCode) {
      searchParams.append('correspondentCode', correspondentCode);
    }
    if (correspondentName) {
      searchParams.append('correspondentName', correspondentName);
    }

    return this.get(`/print?${searchParams.toString()}`);
  }

  // 엑셀 다운로드용 전체 데이터
  getAllCorrespondentItemList({ correspondentCode, itemUniqueCode, itemName }) {
    const searchParams = new URLSearchParams();
    searchParams.append('correspondentCode', correspondentCode);

    if (itemUniqueCode) {
      searchParams.append('itemUniqueCode', itemUniqueCode);
    }
    if (itemName) {
      searchParams.append('itemName', itemName);
    }

    return this.get(`/print-items?${searchParams.toString()}`);
  }

  // 거래처 상세 정보 조회
  getCorrespondent(correspondentCode) {
    return this.get(`/${correspondentCode}`);
  }

  //
  // POST
  //

  // 거래처 등록
  createCorrespondent({ name, address, detailAddress, email, contact, managerName }) {
    return this.post('/create', {
      name,
      address,
      detailAddress,
      email,
      contact,
      managerName,
    });
  }

  //
  // PUT
  //

  // 거래처 수정
  editCorrespondent(correspondentCode, { name, address, detailAddress, email, contact, managerName }) {
    return this.put(`/edit/${correspondentCode}`, {
      name,
      address,
      detailAddress,
      email,
      contact,
      managerName,
    });
  }

  //
  // DELETE
  //

  // 거래처 삭제
  deleteCorrespondent(correspondentCode) {
    return this.delete('/delete', {
      correspondentCode,
    });
  }
}
