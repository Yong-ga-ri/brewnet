import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class HQPurchaseApi extends BaseApiService {
  constructor() {
    super('/v1/hq/purchase');
  }

  //
  // GET
  //

  // 발주목록 조회
  getPurchases({
    page = 1,
    pageSize = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    purchaseCode = null,
    memberName = '',
    correspondentName = '',
    storageName = '',
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('pageNumber', page);
    searchParams.append('pageSize', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (purchaseCode) {
      searchParams.append('purchaseCode', purchaseCode);
    }

    if (memberName) {
      searchParams.append('memberName', memberName);
    }

    if (correspondentName) {
      searchParams.append('correspondentName', correspondentName);
    }

    if (storageName) {
      searchParams.append('storageName', storageName);
    }

    return this.get(`?${searchParams.toString()}`);
  }

  // 발주 상세 조회
  getPurchase(purchaseCode) {
    return this.get(`/${purchaseCode}`);
  }

  // 특정 발주의 결재라인 조회
  getPurchaseApprovalLines(purchaseCode) {
    return this.get(`/${purchaseCode}/approval-line`);
  }

  // 발주 결재라인의 결재자 후보들 조회
  getPurchaseApproverCandidates() {
    return this.get('/approvers?approvalLine=PURCHASE');
  }

  // 전체 입고품목 목록 조회(발주 품목)
  getInStockItems({
    page = 1,
    pageSize = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    itemUniqueCode,
    itemName,
    correspondentName,
    storageName,
    onlyUnchecked = false,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('pageNumber', page);
    searchParams.append('pageSize', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (itemUniqueCode) {
      searchParams.append('itemUniqueCode', itemUniqueCode);
    }

    if (itemName) {
      searchParams.append('itemName', itemName);
    }

    if (correspondentName) {
      searchParams.append('correspondentName', correspondentName);
    }

    if (storageName) {
      searchParams.append('storageName', storageName);
    }

    if (onlyUnchecked) {
      return this.get(`/uncheck-in-stock?${searchParams.toString()}`);
    }
    return this.get(`/total-in-stock?${searchParams.toString()}`);
  }

  //
  // POST
  //

  // 발주(구매품의서) 등록
  createPurchase({ comment, correspondentCode, storageCode, approverCode, items }) {
    return this.post('/create', {
      comment,
      correspondentCode,
      storageCode,
      approverCode,
      items,
    });
  }

  //
  // PUT
  //

  // 입고 처리
  stockIn({ itemCode, purchaseCode }) {
    return this.put('/in-stock', { itemCode, purchaseCode });
  }

  // 발주 결재 승인
  approvePurchase({ purchaseCode, comment }) {
    return this.put(`/approve/${purchaseCode}`, {
      comment,
    });
  }

  // 발주 결재 반려
  rejectPurchase({ purchaseCode, comment }) {
    return this.put(`/reject/${purchaseCode}`, {
      comment,
    });
  }
}
