import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';
import { SEARCH_CRITERIA } from '../constant';

export default class HQExchangeApi extends BaseApiService {
  constructor() {
    super('/v1/hq/exchange');
  }

  //
  // GET
  //

  // 엑셀 다운로드용 교환목록 조회
  getExchangesExcelData({
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
    getConfirmed = false, // false: 전체조회, true: 미결재건 조회
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      switch (criteria) {
        case SEARCH_CRITERIA.EXCHANGE_CODE:
          criteria = 'exchangeCode';
          break;
        case SEARCH_CRITERIA.EXCHANGE_FRANCHISE_NAME:
          criteria = 'franchiseName';
          break;
        case SEARCH_CRITERIA.EXCHANGE_MANAGER_NAME:
          criteria = 'managerName';
          break;
        default:
          break;
      }
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    searchParams.append('getConfirmed', getConfirmed);

    return this.get(`/excel-data?${searchParams.toString()}`);
  }

  // 교환목록 검색
  searchExchanges({
    page = 0,
    size = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
    getConfirmed = false, // false: 전체조회, true: 미결재건 조회
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', size);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      switch (criteria) {
        case SEARCH_CRITERIA.EXCHANGE_CODE:
          criteria = 'exchangeCode';
          break;
        case SEARCH_CRITERIA.EXCHANGE_FRANCHISE_NAME:
          criteria = 'franchiseName';
          break;
        case SEARCH_CRITERIA.EXCHANGE_MANAGER_NAME:
          criteria = 'managerName';
          break;
        default:
          break;
      }
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    searchParams.append('getConfirmed', getConfirmed);

    return this.get(`/search?${searchParams.toString()}`);
  }

  // 교환 상세조회
  getExchangeDetail(exchangeCode) {
    return this.get(`/${exchangeCode}`);
  }

  // 교환 결재이력 조회
  getExchangeApproverList(exchangeCode) {
    return this.get(`/approver/${exchangeCode}`);
  }

  // 타부서 교환처리내역 목록 조회/검색
  searchOtherExchanges({
    page = 0,
    size = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    criteria,
    keyword,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', size);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    if (criteria && keyword) {
      searchParams.append('searchFilter', criteria);
      searchParams.append('searchWord', keyword);
    }

    return this.get(`/other?${searchParams.toString()}`);
  }

  // 타부서 교환처리내역 상세조회
  getExchangeOtherDetail(exchangeStockHistoryCode) {
    return this.get(`/other/${exchangeStockHistoryCode}`);
  }

  //
  // POST
  //

  // 기안자의 교환요청 승인/반려
  requestApproval({ exchangeCode, approved, comment, approverCodeList }) {
    return this.post(`/${exchangeCode}/drafter-approve`, {
      approval: approved,
      comment,
      approverCodeList,
    });
  }

  // 기안자의 교환요청 취소
  cancelRequestApproval(exchangeCode) {
    console.log('exchangeCode', exchangeCode);
    return this.post(`/cancel-approve/${exchangeCode}`);
  }

  // 책임자의 결재 승인/반려
  managerApprove({ exchangeCode, approval, comment }) {
    return this.post(`/${exchangeCode}/manager-approve`, { approval, comment });
  }

  // 타부서 교환처리내역 처리 - 교환완료
  completeExchange(exchangeStockHistoryCode) {
    return this.post(`/other/complete/${exchangeStockHistoryCode}`);
  }

  // 결재자의 결재 승인/반려
  approval({ exchangeCode, approved, comment }) {
    return this.post(`/${exchangeCode}/manager-approve`, {
      approval: approved,
      comment,
    });
  }
}
