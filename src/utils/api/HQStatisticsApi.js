import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class HQStatisticsApi extends BaseApiService {
  constructor() {
    super('/v1/hq/statistics');
  }

  //
  // GET
  //

  // 안전 재고 위험 목록
  getWarningSafetyStock() {
    const searchParams = new URLSearchParams();
    searchParams.append('page', 0);
    searchParams.append('size', 5); // 5개만 조회

    return this.get(`/safe-stock?${searchParams.toString()}`);
  }

  // 주문 통계
  getOrderGraphData(startDate, endDate) {
    const searchParams = new URLSearchParams();
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).format('YYYY-MM-DD'));

    return this.get(`/order?${searchParams.toString()}`);
  }

  // 달력용 데이터
  getOrderCalendarData(year, month) {
    const searchParams = new URLSearchParams();
    searchParams.append(
      'yearMonth',
      dayjs()
        .set('year', year)
        .set('month', month - 1)
        .format('YYYY-MM'),
    );

    return this.get(`/order-quantity-price?${searchParams.toString()}`);
  }

  // 신규 주문 목록
  getNewOrders() {
    const searchParams = new URLSearchParams();
    searchParams.append('page', 0);
    searchParams.append('size', 5); // 5개만 조회

    return this.get(`/new-order?${searchParams.toString()}`);
  }

  // 나의 결재 대기 목록
  getDraftListOnWaitingMyApproval() {
    const searchParams = new URLSearchParams();
    searchParams.append('page', 0);
    searchParams.append('size', 5); // 5개만 조회

    return this.get(`/my-wait-approval?${searchParams.toString()}`);
  }
}
