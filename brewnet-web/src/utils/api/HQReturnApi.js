import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';
import { SEARCH_CRITERIA } from '../constant';

export default class HQReturnApi extends BaseApiService {
  constructor() {
    super('/v1/hq/return');
  }

  //
  // GET
  //

  // 반품요청 목록 조회
  getReturnList({
    page = 0,
    pageSize = 15,
    startDate,
    endDate,
    criteria,
    keyword,
    getConfirmed = false, // false: 전체조회, true: 미결재건 조회
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    searchParams.append('getConfirmed', getConfirmed);

    return this.get(`/search?${searchParams.toString()}`);
  }

  // 전체 반품 목록 (엑셀 다운로드용)
  getAllReturnList({
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
    getConfirmed,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    searchParams.append('getConfirmed', getConfirmed);

    return this.get(`/excel-data?${searchParams.toString()}`);
  }

  // 반품상세조회
  getReturnDetail(returnCode) {
    return this.get(`/${returnCode}`);
  }

  // 반품의 결재내역 조회
  getReturnApproverList(returnCode) {
    return this.get(`/approver/${returnCode}`);
  }

  // 재고부서 처리내역 조회
  getStockDeptList({ page = 0, pageSize = 15, startDate, endDate, criteria, keyword }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    return this.get(`/other/return?${searchParams.toString()}`);
  }

  // 회계부서 처리내역 조회
  getAccountDeptList({ page = 0, pageSize = 15, startDate, endDate, criteria, keyword }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      let criteriaKey = criteria;
      if (criteria === SEARCH_CRITERIA.RETURN_ACCOUNT_DEPT_RETURN_CODE) {
        criteriaKey = 'code';
      } else if (criteria === SEARCH_CRITERIA.RETURN_ACCOUNT_DEPT_MANAGER) {
        criteriaKey = 'manager';
      }
      searchParams.append('searchFilter', criteriaKey);
      searchParams.append('searchWord', keyword);
    }

    return this.get(`/other/refund?${searchParams.toString()}`);
  }

  // 반품의 재고처리내역 상세 조회
  getReturnStockDetail(returningStockHistoryCode) {
    return this.get(`/other/return/${returningStockHistoryCode}`);
  }

  // 반품의 환불처리내역 상세 조회
  getReturnRefundDetail(returningRefundHistoryCode) {
    return this.get(`/other/refund/${returningRefundHistoryCode}`);
  }

  //
  // POST
  //

  // 기안자의 결재요청
  requestApproval({ returnCode, approved, comment, approverCodeList }) {
    return this.post(`/${returnCode}/drafter-approve`, {
      approval: approved,
      comment,
      approverCodeList,
    });
  }

  // 기안자의 결재요청 취소
  cancelRequestApproval(returnCode) {
    return this.post(`/cancel-approve/${returnCode}`);
  }

  // 결재자의 결재처리
  approval({ returnCode, approved, comment }) {
    return this.post(`/${returnCode}/manager-approve`, {
      approval: approved,
      comment,
    });
  }

  // 본사의 환불처리완료
  confirmRefund(returningRefundHistoryCode) {
    return this.post(`/other/refund-complete/${returningRefundHistoryCode}`);
  }

  // 본사의 반품처리완료
  confirmStock(returningStockHistoryCode) {
    return this.post(`/other/stock-complete/${returningStockHistoryCode}`);
  }
}
