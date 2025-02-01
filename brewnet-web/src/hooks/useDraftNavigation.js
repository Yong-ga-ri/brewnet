import { useRouter } from 'vue-router';

import { DRAFT_KIND } from '@/utils/constant';

export function useDraftNavigation() {
  const router = useRouter();

  const clickGoDetail = data => {
    // 기안 타입에 따라 상세보기 페이지 이동
    if (data.kind === DRAFT_KIND.ORDER) {
      // 주문 상세로 이동
      router.push({ name: 'hq:order:detail', params: { orderCode: data.code } });
    } else if (data.kind === DRAFT_KIND.EXCHANGE) {
      // 교환 상세로 이동
      router.push({ name: 'hq:order:exchange:detail', params: { exchangeCode: data.code } });
    } else if (data.kind === DRAFT_KIND.RETURN) {
      // 반품 상세로 이동
      router.push({ name: 'hq:order:return:detail', params: { returnCode: data.code } });
    } else if (data.kind === DRAFT_KIND.PURCHASE) {
      // 발주 상세로 이동
      router.push({ name: 'hq:purchase:detail', params: { purchaseCode: data.code } });
    }
  };

  return { clickGoDetail };
}
