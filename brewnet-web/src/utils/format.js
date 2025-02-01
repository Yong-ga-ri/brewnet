import {
  DRAFT_KIND,
  APPROVAL_STATUS,
  EXCHANGE_STATUS,
  ORDER_STATUS,
  POSITION,
  PURCHASE_STATUS,
  RETURN_STATUS,
  ROLE,
  DRAFTER_APPROVED,
  APPROVAL,
  SEARCH_CRITERIA,
  APPROVER_APPROVED_STATUS,
  DELIVERY_STATUS,
  RETURN_REASON,
  RETURN_STOCK_STATUS,
  OTHER_DEPT_CHECK_STATUS,
  RETURN_REFUND_STATUS,
  EXCHANGE_OTHER_STATUS,
  CONFIRMED_STATUS,
} from './constant';

export const formatKoEmployeePosition = position => {
  switch (position) {
    case POSITION.STAFF:
      return '사원';

    case POSITION.ASSISTANT_MANAGER:
      return '대리';

    case POSITION.MANAGER:
      return '과장';

    case POSITION.CEO:
      return '대표';

    default:
      return '';
  }
};

export const formatKoMemberRole = role => {
  switch (role) {
    case ROLE.MASTER:
      return '마스터';

    case ROLE.RESPONSIBLE_ADMIN:
      return '책임관리자';

    case ROLE.GENERAL_ADMIN:
      return '일반관리자';

    case ROLE.FRANCHISE:
      return '가맹점';

    case ROLE.DELIVERY:
      return '배송';

    default:
      return '';
  }
};

export const formatKoExchangeReason = reason => {
  switch (reason) {
    case 'DAMAGED':
      return '파손';

    case 'DEFECTIVE':
      return '품질불량';

    case 'OTHER':
      return '기타';

    default:
      return '';
  }
};

export const formatKoExchangeStatus = status => {
  switch (status) {
    case EXCHANGE_STATUS.REQUESTED:
      return '교환요청';

    case EXCHANGE_STATUS.PENDING:
      return '교환처리중';

    case EXCHANGE_STATUS.CANCELED:
      return '취소';

    case EXCHANGE_STATUS.APPROVED:
      return '교환승인';

    case EXCHANGE_STATUS.REJECTED:
      return '교환반려';

    case EXCHANGE_STATUS.PICKING:
      return '수거중';

    case EXCHANGE_STATUS.PICKED:
      return '수거완료';

    case EXCHANGE_STATUS.SHIPPING:
      return '배송중';

    case EXCHANGE_STATUS.SHIPPED:
      return '배송완료';

    case EXCHANGE_STATUS.COMPLETED:
      return '배송완료';

    default:
      return '';
  }
};

export const formatKoPurchaseStatus = status => {
  switch (status) {
    case PURCHASE_STATUS.REQUESTED:
      return '결재요청';

    case PURCHASE_STATUS.CANCELED:
      return '결재요청취소';

    case PURCHASE_STATUS.APPROVED:
      return '발주승인';

    case PURCHASE_STATUS.REJECTED:
      return '발주반려';

    default:
      return '';
  }
};

export const formatKoDraftKind = kind => {
  switch (kind) {
    case DRAFT_KIND.ORDER:
      return '주문';

    case DRAFT_KIND.PURCHASE:
      return '발주';

    case DRAFT_KIND.EXCHANGE:
      return '교환';

    case DRAFT_KIND.RETURN:
      return '반품';

    default:
      return '';
  }
};

export const formatKoApprovalStatus = status => {
  switch (status) {
    case APPROVAL_STATUS.UNCONFIRMED:
      return '진행중';

    case APPROVAL_STATUS.CANCELED:
      return '결재취소';

    case APPROVAL_STATUS.APPROVED:
      return '승인';

    case APPROVAL_STATUS.REJECTED:
      return '반려';

    default:
      return '';
  }
};

export const formatKoApproverApprovedStatus = status => {
  switch (status) {
    case APPROVER_APPROVED_STATUS.UNCONFIRMED:
      return '대기';

    case APPROVER_APPROVED_STATUS.APPROVED:
      return '승인';

    case APPROVER_APPROVED_STATUS.REJECTED:
      return '반려';

    default:
      return '';
  }
};

export const formatKoDrafterApproved = drafterApproved => {
  switch (drafterApproved) {
    case DRAFTER_APPROVED.APPROVE:
      return '승인';

    case DRAFTER_APPROVED.REJECT:
      return '반려';

    case DRAFTER_APPROVED.NONE:
      return '미정';

    default:
      return '';
  }
};

export const formatKoApproval = approval => {
  switch (approval) {
    case APPROVAL.APPROVED:
      return '결재승인';

    case APPROVAL.CANCELED:
      return '결재취소';

    case APPROVAL.UNCONFIRMED:
      return '미확인'; // 결재 확인되지 않음

    case APPROVAL.REJECTED:
      return '결재반려';

    default:
      return '';
  }
};

export const formatKoOrderStatus = status => {
  switch (status) {
    case ORDER_STATUS.REQUESTED:
      return '주문요청';

    case ORDER_STATUS.PENDING:
      return '처리중';

    case ORDER_STATUS.CANCELED:
      return '주문취소';

    case ORDER_STATUS.APPROVED:
      return '주문승인';

    case ORDER_STATUS.REJECTED:
      return '주문반려';

    case ORDER_STATUS.SHIPPING:
      return '배송중';

    case ORDER_STATUS.SHIPPED:
      return '배송완료';

    default:
      return '';
  }
};

export const formatKoSearchCriteria = criteria => {
  switch (criteria) {
    case SEARCH_CRITERIA.ITEM_UNIQUE_CODE:
      return '상품코드';

    case SEARCH_CRITERIA.ITEM_NAME:
    case SEARCH_CRITERIA.RETURN_ITEM_NAME:
      return '상품명';

    case SEARCH_CRITERIA.CORRESPONDENT_NAME:
      return '거래처명';

    case SEARCH_CRITERIA.CORRESPONDENT_CODE:
      return '거래처코드';

    case SEARCH_CRITERIA.STORAGE_NAME:
      return '창고명';

    case SEARCH_CRITERIA.STORAGE_CODE:
      return '창고코드';

    case SEARCH_CRITERIA.ORDER_CODE:
      return '주문번호';

    case SEARCH_CRITERIA.ORDER_MANAGER:
      return '주문담당자';

    case SEARCH_CRITERIA.ORDERED_FRANCHISE_NAME:
      return '주문지점';

    case SEARCH_CRITERIA.EXCHANGE_CODE:
      return '교환번호';

    case SEARCH_CRITERIA.EXCHANGE_FRANCHISE_NAME:
      return '교환지점';

    case SEARCH_CRITERIA.EXCHANGE_MANAGER_NAME:
      return '교환담당자';

    case SEARCH_CRITERIA.RETURN_CODE:
      return '반품코드';

    case SEARCH_CRITERIA.RETURN_FRANCHISE_NAME:
      return '반품지점';

    case SEARCH_CRITERIA.RETURN_MANAGER_NAME:
    case SEARCH_CRITERIA.RETURN_MANGER_IN_OTHER_DEPT:
      return '반품담당자';

    case SEARCH_CRITERIA.RETURN_STOCK_DEPT_MANAGER:
      return '재고담당자';

    case SEARCH_CRITERIA.RETURN_STOCK_DEPT_RETURN_CODE:
      return '재고처리코드';

    case SEARCH_CRITERIA.RETURN_ACCOUNT_DEPT_MANAGER:
      return '환불담당자';

    case SEARCH_CRITERIA.RETURN_ACCOUNT_DEPT_RETURN_CODE:
      return '환불처리코드';

    default:
      return '';
  }
};

export const formatKoReturnStatus = status => {
  switch (status) {
    case RETURN_STATUS.REQUESTED:
      return '반품요청';

    case RETURN_STATUS.PENDING:
      return '반품처리중';

    case RETURN_STATUS.CANCELED:
      return '취소';

    case RETURN_STATUS.APPROVED:
      return '반품승인';

    case RETURN_STATUS.REJECTED:
      return '반품반려';

    case RETURN_STATUS.PICKING:
      return '회수중';

    case RETURN_STATUS.PICKED:
      return '회수완료';

    case RETURN_STATUS.COMPLETED:
      return '반품완료';

    default:
      return '';
  }
};

export const formatKoDeliveryStatus = status => {
  switch (status) {
    case DELIVERY_STATUS.START_DELIVERY:
      return '배송 시작';

    case DELIVERY_STATUS.SHIPPING:
      return '배송중';

    case DELIVERY_STATUS.SHIPPED:
      return '배송 완료';

    case DELIVERY_STATUS.START_PICK:
      return '회수 시작';

    case DELIVERY_STATUS.PICKING:
      return '회수중';

    case DELIVERY_STATUS.PICKED:
      return '회수 완료';

    default:
      return '';
  }
};

export const formatBusinessNumber = rawBusinessNumber => {
  if (rawBusinessNumber.length !== 10) return rawBusinessNumber;

  const front = rawBusinessNumber.slice(0, 3);
  const middle = rawBusinessNumber.slice(3, 5);
  const back = rawBusinessNumber.slice(5);
  return `${front}-${middle}-${back}`;
};

export const formatCorporateNumber = rawCorporateNumber => {
  if (rawCorporateNumber.length !== 13) return rawCorporateNumber;

  const start = rawCorporateNumber.slice(0, 6);
  const end = rawCorporateNumber.slice(6);
  return `${start}-${end}`;
};
// 핸드폰 번호
export const formatMobileNumber = mobileNumber => {
  if (mobileNumber.length !== 11) return mobileNumber;

  const front = mobileNumber.slice(0, 3);
  const middle = mobileNumber.slice(3, 7);
  const back = mobileNumber.slice(7);
  return `${front}-${middle}-${back}`;
};

// 서울 지역번호의 전화번호(ex. 02-000-0000)
export const formatSeoulTelephoneNumber = seoulTelephoneNumber => {
  if (seoulTelephoneNumber.length !== 9) return seoulTelephoneNumber;

  const front = seoulTelephoneNumber.slice(0, 2);
  const middle = seoulTelephoneNumber.slice(2, 5);
  const back = seoulTelephoneNumber.slice(5);
  return `${front}-${middle}-${back}`;
};

// 지역번호 앞자리가 3자리인 경우(ex. 031-000-0000)
export const formatLocalTelephoneNumber = localTelephoneNumber => {
  if (localTelephoneNumber.length !== 10) return localTelephoneNumber;

  const front = localTelephoneNumber.slice(0, 3);
  const middle = localTelephoneNumber.slice(3, 6);
  const back = localTelephoneNumber.slice(6);
  return `${front}-${middle}-${back}`;
};

export const formatKoReturnReason = reason => {
  switch (reason) {
    case RETURN_REASON.DAMAGED:
      return '파손';

    case RETURN_REASON.DEFECTIVE:
      return '품질불량';

    case RETURN_REASON.MIND_CHANGE:
      return '단순 변심';

    case RETURN_REASON.OTHER:
      return '기타';

    default:
      return '';
  }
};

export const formatKoReturnStockStatus = status => {
  switch (status) {
    case RETURN_STOCK_STATUS.TOTAL_INBOUND:
      return '전체입고';

    case RETURN_STOCK_STATUS.TOTAL_DISPOSAL:
      return '전체폐기';

    case RETURN_STOCK_STATUS.PARTIAL_INBOUND:
      return '부분입고';

    default:
      return '';
  }
};
export const formatKoExchangeOtherStatus = status => {
  switch (status) {
    case EXCHANGE_OTHER_STATUS.TOTAL_INBOUND:
      return '전체입고';

    case EXCHANGE_OTHER_STATUS.TOTAL_DISPOSAL:
      return '전체폐기';

    case EXCHANGE_OTHER_STATUS.PARTIAL_INBOUND:
      return '부분입고';

    default:
      return '';
  }
};

export const formatKoReturnRefundStatus = status => {
  switch (status) {
    case RETURN_REFUND_STATUS.TOTAL_REFUND:
      return '전체환불';

    case RETURN_REFUND_STATUS.PARTIAL_REFUND:
      return '부분환불';

    case RETURN_REFUND_STATUS.NON_REFUNDABLE:
      return '환불불가';

    default:
      return '';
  }
};

// export const formatKoReturningOtherStatus = status => {
//   switch (status) {
//     case RETURNING_OTHER_STATUS.TOTAL_INBOUND:
//       return '전체입고';

//     case RETURNING_OTHER_STATUS.TOTAL_DISPOSAL:
//       return '전체폐기';

//     case RETURNING_OTHER_STATUS.PARTIAL_INBOUND:
//       return '부분입고';

//     default:
//       return '';
//   }
// };

// export const formatKoRefundOtherStatus = status => {
//   switch (status) {
//     case REFUND_OTHER_STATUS.TOTAL_REFUND:
//       return '전체환불';

//     case REFUND_OTHER_STATUS.PARTIAL_REFUND:
//       return '부분환불';

//     case REFUND_OTHER_STATUS.NON_REFUNDABLE:
//       return '환불불가';

//     default:
//       return '';
//   }
// };

export const formatKoOtherDeptCheckStatus = status => {
  if (status === OTHER_DEPT_CHECK_STATUS.CONFIRMED) return '확인완료';
  if (status === OTHER_DEPT_CHECK_STATUS.UNCONFIRMED) return '미확인';
  return '';
};

export const formatKoConfirmedStatus = status => {
  switch (status) {
    case CONFIRMED_STATUS.CONFIRMED:
      return '처리완료';

    case CONFIRMED_STATUS.UNCONFIRMED:
      return '미처리';

    default:
      return '';
  }
};
