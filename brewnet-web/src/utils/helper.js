import {
  APPROVAL_STATUS,
  EXCHANGE_STATUS,
  ORDER_STATUS,
  PURCHASE_STATUS,
  RETURN_STATUS,
  DRAFTER_APPROVED,
  OTHER_DEPT_CHECK_STATUS,
  EXCHANGE_OTHER_STATUS,
  CONFIRMED_STATUS,
  ORDERED_HQ_ROLES,
} from './constant';

// 프라임뷰에 사용될 데이터
export const makeSelectOption = (label = '', value = '') => {
  return { label, value };
};
export const makeGroupSelectOption = (label = '', value = '', items = []) => {
  return { label, value, items };
};
export const makeRadioOption = (label = '', value = '', inputId = '') => ({ label, value, inputId });
export const makeAutocompleteSuggestion = (code, label) => ({ code, label });
export const makeTabs = (label = '', value = '') => ({ label, value });

// status에 따른 severity
export const getPurchaseStatusSeverity = purchaseStatus => {
  switch (purchaseStatus) {
    case PURCHASE_STATUS.REQUESTED:
      return 'success';

    case PURCHASE_STATUS.CANCELED:
    case PURCHASE_STATUS.REJECTED:
      return 'danger';

    case PURCHASE_STATUS.APPROVED:
      return 'info';

    default:
      return 'info';
  }
};

export const getApprovalStatusSeverity = status => {
  switch (status) {
    case APPROVAL_STATUS.UNCONFIRMED:
      return 'success';

    case APPROVAL_STATUS.REJECTED:
      return 'danger';

    case APPROVAL_STATUS.APPROVED:
      return 'info';

    default:
      return 'secondary';
  }
};

export const getOrderStatusSeverity = orderStatus => {
  switch (orderStatus) {
    case ORDER_STATUS.REQUESTED:
      return 'secondary';

    case ORDER_STATUS.PENDING:
      return 'warn';

    case ORDER_STATUS.CANCELED:
    case ORDER_STATUS.REJECTED:
      return 'danger';

    case ORDER_STATUS.APPROVED:
      return undefined;

    case ORDER_STATUS.SHIPPING:
      return 'info';

    case ORDER_STATUS.SHIPPED:
      return 'success';

    default:
      return undefined;
  }
};
export const getExchangeStatusSeverity = exchangeStatus => {
  switch (exchangeStatus) {
    case EXCHANGE_STATUS.REQUESTED:
      return 'secondary';

    case EXCHANGE_STATUS.PENDING:
      return 'warn';

    case EXCHANGE_STATUS.CANCELED:
    case EXCHANGE_STATUS.REJECTED:
      return 'danger';

    case EXCHANGE_STATUS.APPROVED:
      return 'primary';

    case EXCHANGE_STATUS.PICKING:
    case EXCHANGE_STATUS.PICKED:
    case EXCHANGE_STATUS.SHIPPING:
    case EXCHANGE_STATUS.SHIPPED:
      return 'info';

    case EXCHANGE_STATUS.COMPLETED:
      return 'success';

    default:
      return 'info';
  }
};

export const getDrafterApprovedStatusSeverity = drafterApproved => {
  switch (drafterApproved) {
    case DRAFTER_APPROVED.APPROVE:
      return 'success';

    case DRAFTER_APPROVED.REJECT:
      return 'danger';

    case DRAFTER_APPROVED.NONE:
      return 'info';

    default:
      return 'info';
  }
};
export const getReturnStatusSeverity = status => {
  switch (status) {
    case RETURN_STATUS.REQUESTED:
      return 'secondary';

    case RETURN_STATUS.PENDING:
      return 'warn';

    case RETURN_STATUS.APPROVED:
      return 'primary';

    case RETURN_STATUS.CANCELED:
    case RETURN_STATUS.REJECTED:
      return 'danger';

    case RETURN_STATUS.PICKING:
    case RETURN_STATUS.PICKED:
      return 'info';

    case RETURN_STATUS.COMPLETED:
      return 'success';

    default:
      return undefined;
  }
};

export const getReturnStockCheckStatusSeverity = status => {
  if (status === OTHER_DEPT_CHECK_STATUS.CONFIRMED) return undefined;
  if (status === OTHER_DEPT_CHECK_STATUS.UNCONFIRMED) return 'secondary';
  return undefined;
};

export const makeOrderItemSummary = itemList => {
  if (itemList.length === 1) return itemList[0].name;
  return `${itemList[0].name} 외 ${itemList.length - 1}개`;
};

export const getExchangeOtherStatusSeverity = status => {
  switch (status) {
    case EXCHANGE_OTHER_STATUS.TOTAL_INBOUND:
      return 'success';

    case EXCHANGE_OTHER_STATUS.TOTAL_DISPOSAL:
      return 'danger';

    case EXCHANGE_OTHER_STATUS.PARTIAL_INBOUND:
      return 'info';

    default:
      return 'info';
  }
};
export const getConfirmedStatusSeverity = status => {
  switch (status) {
    case CONFIRMED_STATUS.CONFIRMED:
      return 'success';

    case CONFIRMED_STATUS.UNCONFIRMED:
      return 'danger';

    default:
      return 'info';
  }
};

export const isHQAuthenticated = (memberRole, targetRole) => {
  const memberRoleOrder = ORDERED_HQ_ROLES.indexOf(memberRole);
  const targetRoleOrder = ORDERED_HQ_ROLES.indexOf(targetRole);

  if (memberRoleOrder === -1) return false;

  return memberRoleOrder >= targetRoleOrder; // 권한이 더 높으면 통과
};
