import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class HQOrderApi extends BaseApiService {
  constructor() {
    super('/v1/hq/orders');
  }

  //
  // GET
  //

  // 주문목록 조회
  getOrders({
    page = 1,
    size = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    orderCode = null,
    managerName = '',
    // correspondentName = '',
    franchiseName = '',
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', size);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (orderCode) {
      searchParams.append('orderCode', orderCode);
    }

    if (managerName) {
      searchParams.append('managerName', managerName);
    }

    // if (correspondentName) {
    //   searchParams.append('correspondentName', correspondentName);
    // }

    if (franchiseName) {
      searchParams.append('franchiseName', franchiseName);
    }

    return this.get(`/list?${searchParams.toString()}`);
    // return this.get(`/list`);
  }

  // 주문목록 검색
  searchOrders({
    page = 0,
    size = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
    filter,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', size);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('criteria', criteria);
      searchParams.append('searchWord', keyword);
    }

    if (filter) {
      searchParams.append('filter', filter);
    }

    return this.get(`/search?${searchParams.toString()}`);
  }

  // 전체 주문 목록 (엑셀 다운로드용)
  getAllOrders({
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('criteria', criteria);
      searchParams.append('searchWord', keyword);
    }

    return this.get(`/excel?${searchParams.toString()}`);
  }

  // 주문 상세 조회 설정
  getOrderDetail(orderCode) {
    return this.get(`/detail/${orderCode}`);
  }

  // 주문 결재이력 조회
  getOrderApprovalLines(orderCode) {
    return this.get(`/detail/${orderCode}/history/approval`);
  }

  //
  // POST
  //

  // 결재요청
  requestApproval({ orderCode, superManagerMemberCode, comment }) {
    return this.post(`/request/${orderCode}`, {
      superManagerMemberCode,
      comment,
    });
  }

  // 기안자가 주문반려
  rejectDraft({ orderCode, comment }) {
    return this.post(`/reject/${orderCode}`, { reason: comment });
  }

  //
  // DELETE
  //

  // 기안자의 결재요청 취소
  cancelApproval(orderCode) {
    return this.delete(`/request/cancel/${orderCode}`);
  }
}
