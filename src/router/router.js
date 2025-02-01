import { createRouter, createWebHistory } from 'vue-router';

import { useUserStore } from '@/stores/user';
import { ROLE } from '@/utils/constant';

/**
 * meta: {
 *   requiresAuth: true, // 인증이 필요한 페이지
 *   guestOnly: true,    // 로그인한 사용자는 접근할 수 없는 페이지
 * }
 */

const routes = [
  {
    path: '/auth',
    name: 'auth:index',
    component: () => import('@/views/AuthIndexView.vue'),
    meta: {
      guestOnly: true,
    },
    children: [
      {
        path: 'login',
        name: 'auth:login',
        component: () => import('@/views/auth/LoginView.vue'),
        meta: {
          guestOnly: true,
        },
      },
      {
        path: 'find-password',
        name: 'auth:find-password',
        component: () => import('@/views/auth/FindPassword.vue'),
        meta: {
          guestOnly: true,
        },
      },
      {
        path: 'reset-password',
        name: 'auth:reset-password',
        component: () => import('@/views/auth/ResetPassword.vue'),
        meta: {
          guestOnly: true,
        },
      },
    ],
  },

  // 본사 화면
  {
    path: '/hq',
    name: 'hq:index',
    component: () => import('@/views/HeadQuarterIndexView.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: 'home',
        name: 'hq:home',
        component: () => import('@/views/headQuarter/HomeView.vue'),
      },
      {
        path: 'purchase',
        name: 'hq:purchase',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '발주',
        },
        children: [
          {
            path: 'list',
            name: 'hq:purchase:list',
            component: () => import('@/views/headQuarter/PurchaseListView.vue'),
            meta: {
              breadcrumb: '발주 내역 조회',
            },
          },
          {
            path: ':purchaseCode/detail',
            name: 'hq:purchase:detail',
            component: () => import('@/views/headQuarter/PurchaseDetailView.vue'),
            meta: {
              breadcrumb: '발주 상세',
              backTo: true,
            },
          },
          {
            path: 'form',
            name: 'hq:purchase:form',
            component: () => import('@/views/headQuarter/PurchaseFormView.vue'),
            meta: {
              breadcrumb: '발주 입력',
            },
          },
          {
            path: 'stock-arrival',
            name: 'hq:purchase:stock-arrival',
            component: () => import('@/views/headQuarter/PurchaseStockArrivalListView.vue'),
            meta: {
              breadcrumb: '입고 확인',
            },
          },
        ],
      },
      {
        path: 'order',
        name: 'hq:order',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '주문/교환/반품',
        },
        children: [
          {
            path: 'list',
            name: 'hq:order:list',
            component: () => import('@/views/headQuarter/OrderListView.vue'),
            meta: {
              breadcrumb: '주문요청 관리',
            },
          },
          {
            path: ':orderCode/detail',
            name: 'hq:order:detail',
            component: () => import('@/views/headQuarter/OrderDetailView.vue'),
            meta: {
              breadcrumb: '주문요청 상세',
              backTo: true,
            },
          },
          {
            path: 'exchange/list',
            name: 'hq:order:exchange:list',
            component: () => import('@/views/headQuarter/ExchangeListView.vue'),
            meta: {
              breadcrumb: '교환요청 관리',
            },
          },
          {
            path: 'exchange/:exchangeCode/detail',
            name: 'hq:order:exchange:detail',
            component: () => import('@/views/headQuarter/ExchangeDetailView.vue'),
            meta: {
              breadcrumb: '교환요청 상세',
              backTo: true,
            },
          },
          {
            path: 'exchange/other-list',
            name: 'hq:order:exchange:other-list',
            component: () => import('@/views/headQuarter/ExchangeOtherListView.vue'),
            meta: {
              breadcrumb: '교환 처리내역 조회',
            },
          },
          {
            path: 'exchange/:exchangeStockHistoryCode/other-detail',
            name: 'hq:order:exchange:other-detail',
            component: () => import('@/views/headQuarter/ExchangeOtherDetailView.vue'),
            meta: {
              breadcrumb: '교환처리 상세',
              backTo: true,
            },
          },
          {
            path: 'return/list',
            name: 'hq:order:return:list',
            component: () => import('@/views/headQuarter/ReturnListView.vue'),
            meta: {
              breadcrumb: '반품요청 관리',
            },
          },
          {
            path: 'return/:returnCode/detail',
            name: 'hq:order:return:detail',
            component: () => import('@/views/headQuarter/ReturnDetailView.vue'),
            meta: {
              breadcrumb: '반품요청 상세',
              backTo: true,
            },
          },
          {
            path: 'return/stock/list',
            name: 'hq:order:return:other-dept-stock',
            component: () => import('@/views/headQuarter/ReturnOtherStockDeptView.vue'),
            meta: {
              breadcrumb: '반품 처리내역 조회',
            },
          },
          {
            path: 'return/refund/list',
            name: 'hq:order:return:other-dept-refund',
            component: () => import('@/views/headQuarter/ReturnOtherAccountDeptView.vue'),
            meta: {
              breadcrumb: '환불 처리내역 조회',
            },
          },
          {
            path: 'return/stock/:detailCode/detail',
            name: 'hq:order:return:stock-detail',
            component: () => import('@/views/headQuarter/ReturnStockDetailView.vue'),
            meta: {
              breadcrumb: '반품 재고처리 상세',
              backTo: true,
            },
          },
          {
            path: 'return/refund/:detailCode/detail',
            name: 'hq:order:return:refund-detail',
            component: () => import('@/views/headQuarter/ReturnRefundDetailView.vue'),
            meta: {
              breadcrumb: '반품 환불처리 상세',
              backTo: true,
            },
          },
        ],
      },
      {
        path: 'partner',
        name: 'hq:partner',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '가맹점/거래처',
        },
        children: [
          {
            path: 'franchise/list',
            name: 'hq:partner:franchise:list',
            component: () => import('@/views/headQuarter/FranchiseListView.vue'),
            meta: {
              breadcrumb: '가맹점 조회',
            },
          },
          {
            path: 'franchise/create',
            name: 'hq:partner:franchise:create',
            component: () => import('@/views/headQuarter/FranchiseFormView.vue'),
            meta: {
              breadcrumb: '가맹점 등록',
              auth: ROLE.RESPONSIBLE_ADMIN,
            },
          },
          {
            path: 'franchise/:franchiseCode/edit',
            name: 'hq:partner:franchise:edit',
            component: () => import('@/views/headQuarter/FranchiseFormView.vue'),
            meta: {
              breadcrumb: '가맹점 수정',
              backTo: true,
              auth: ROLE.RESPONSIBLE_ADMIN,
            },
          },
          {
            path: 'franchise-account/list',
            name: 'hq:partner:franchise-account:list',
            component: () => import('@/views/headQuarter/FranchiseAccountListView.vue'),
            meta: {
              breadcrumb: '가맹점 계정 조회',
            },
          },
          {
            path: 'franchise-account/create',
            name: 'hq:partner:franchise-account:create',
            component: () => import('@/views/headQuarter/FranchiseAccountFormView.vue'),
            meta: {
              breadcrumb: '가맹점 계정 등록',
            },
          },
          {
            path: 'franchise-account/:memberCode/edit',
            name: 'hq:partner:franchise-account:edit',
            component: () => import('@/views/headQuarter/FranchiseAccountFormView.vue'),
            meta: {
              breadcrumb: '가맹점 계정 수정',
              backTo: true,
            },
          },
          {
            path: 'correspondent/list',
            name: 'hq:partner:correspondent:list',
            component: () => import('@/views/headQuarter/CorrespondentListView.vue'),
            meta: {
              breadcrumb: '거래처 조회',
            },
          },
          {
            path: 'correspondent/create',
            name: 'hq:partner:correspondent:create',
            component: () => import('@/views/headQuarter/CorrespondentFormView.vue'),
            meta: {
              breadcrumb: '거래처 등록',
            },
          },
          {
            path: 'correspondent/edit/:correspondentCode',
            name: 'hq:partner:correspondent:edit',
            component: () => import('@/views/headQuarter/CorrespondentFormView.vue'),
            meta: {
              breadcrumb: '거래처 수정',
              backTo: true,
            },
          },
        ],
      },
      {
        path: 'stock',
        name: 'hq:stock',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '창고/상품',
        },
        children: [
          {
            path: 'storage-stock',
            name: 'hq:stock:storage-stock',
            component: () => import('@/views/headQuarter/StockListByStorageView.vue'),
            meta: {
              breadcrumb: '창고별 재고 조회',
            },
          },
          {
            path: 'storage/list',
            name: 'hq:stock:storage:list',
            component: () => import('@/views/headQuarter/StorageListView.vue'),
            meta: {
              breadcrumb: '창고 목록',
            },
          },
          {
            path: 'storage/:storageCode/detail',
            name: 'hq:stock:storage:detail',
            component: () => import('@/views/headQuarter/StorageDetailView.vue'),
            meta: {
              breadcrumb: '창고 상세보기',
              backTo: true,
            },
          },
          {
            path: 'storage/create',
            name: 'hq:stock:storage:create',
            component: () => import('@/views/headQuarter/StorageFormView.vue'),
            meta: {
              breadcrumb: '창고 등록',
            },
          },
          {
            path: 'storage/edit/:storageCode',
            name: 'hq:stock:storage:edit',
            component: () => import('@/views/headQuarter/StorageFormView.vue'),
            meta: {
              breadcrumb: '창고 수정',
              backTo: true,
            },
          },
          {
            path: 'item-category',
            name: 'hq:stock:item-category',
            component: () => import('@/views/headQuarter/ItemCategoryView.vue'),
            meta: {
              breadcrumb: '상품 카테고리 관리',
            },
          },
          {
            path: 'item/list',
            name: 'hq:stock:item:list',
            component: () => import('@/views/headQuarter/ItemListView.vue'),
            meta: {
              breadcrumb: '상품 조회',
            },
          },
          {
            path: 'item/create',
            name: 'hq:stock:item:create',
            component: () => import('@/views/headQuarter/ItemFormView.vue'),
            meta: {
              breadcrumb: '상품 등록',
            },
          },
          {
            path: 'item/:itemCode/edit',
            name: 'hq:stock:item:edit',
            component: () => import('@/views/headQuarter/ItemFormView.vue'),
            meta: {
              breadcrumb: '상품 수정',
              backTo: true,
            },
          },
          {
            path: 'item/must-buy/list',
            name: 'hq:stock:item-must-buy:list',
            component: () => import('@/views/headQuarter/ItemMustBuyListView.vue'),
            meta: {
              breadcrumb: '필수구매상품 관리',
            },
          },
        ],
      },
      {
        path: 'approval',
        name: 'hq:approval',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
      },
      {
        path: 'board',
        name: 'hq:board',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '게시판',
        },
        children: [
          {
            path: 'notice/list',
            name: 'hq:board:notice:list',
            component: () => import('@/views/headQuarter/NoticeListView.vue'),
            meta: {
              breadcrumb: '공지사항 조회',
            },
          },
          {
            path: 'notice/:noticeCode/detail',
            name: 'hq:board:notice:detail',
            component: () => import('@/views/headQuarter/NoticeDetailView.vue'),
            meta: {
              breadcrumb: '공지사항 상세',
              backTo: true,
            },
          },
          {
            path: 'notice/create',
            name: 'hq:board:notice:create',
            component: () => import('@/views/headQuarter/NoticeFormView.vue'),
            meta: {
              breadcrumb: '공지사항 등록',
              auth: ROLE.MASTER,
            },
          },
          {
            path: 'notice/:noticeCode/edit',
            name: 'hq:board:notice:edit',
            component: () => import('@/views/headQuarter/NoticeFormView.vue'),
            meta: {
              breadcrumb: '공지사항 수정',
              backTo: true,
              auth: ROLE.MASTER,
            },
          },
        ],
      },
      {
        path: 'settings',
        name: 'hq:settings',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '시스템 설정',
        },
        children: [
          {
            path: 'employee/list',
            name: 'hq:settings:employee:list',
            component: () => import('@/views/headQuarter/EmployeeListView.vue'),
            meta: {
              breadcrumb: '임직원 조회',
            },
          },
          {
            path: 'employee/create',
            name: 'hq:settings:employee:create',
            component: () => import('@/views/headQuarter/EmployeeFormView.vue'),
            meta: {
              breadcrumb: '임직원 등록',
            },
          },
          {
            path: 'employee/:memberCode/edit',
            name: 'hq:settings:employee:edit',
            component: () => import('@/views/headQuarter/EmployeeFormView.vue'),
            meta: {
              breadcrumb: '임직원 수정',
              backTo: true,
            },
          },
          {
            path: 'company',
            name: 'hq:settings:company',
            component: () => import('@/views/headQuarter/CompanyInfoView.vue'),
            meta: {
              breadcrumb: '회사 정보',
            },
          },
          {
            path: 'company-seal',
            name: 'hq:settings:company-seal',
            component: () => import('@/views/headQuarter/CompanySealView.vue'),
            meta: {
              breadcrumb: '법인인감 조회',
            },
          },
          {
            path: 'company-seal/upload',
            name: 'hq:settings:company-seal:upload',
            component: () => import('@/views/headQuarter/CompanySealUploadView.vue'),
            meta: {
              breadcrumb: '법인인감 등록',
              auth: ROLE.MASTER,
            },
          },
          {
            path: 'company-seal/history',
            name: 'hq:settings:company-seal:history',
            component: () => import('@/views/headQuarter/CompanySealHistoryView.vue'),
            meta: {
              breadcrumb: '법인인감 사용내역',
            },
          },
          {
            path: 'approval-line',
            name: 'hq:settings:approval-line',
            component: () => import('@/views/headQuarter/ApprovalLineView.vue'),
            meta: {
              breadcrumb: '결재라인 조회',
            },
          },
        ],
      },
      {
        path: 'my',
        name: 'hq:my',
        component: () => import('@/components/headQuarter/HeadQuarterPageBody.vue'),
        meta: {
          breadcrumb: '마이페이지',
        },
        children: [
          {
            path: 'info',
            name: 'hq:my:info',
            component: () => import('@/views/headQuarter/MyInfoView.vue'),
            meta: { breadcrumb: '내 정보 조회' },
          },
          {
            path: 'change-password',
            name: 'hq:my:change-password',
            component: () => import('@/views/headQuarter/ChangePasswordView.vue'),
            meta: { breadcrumb: '비밀번호 변경' },
          },
          {
            path: 'upload-signature',
            name: 'hq:my:upload-signature',
            component: () => import('@/views/headQuarter/SignatureUploadView.vue'),
            meta: { breadcrumb: '서명 등록' },
          },
          {
            path: 'draft',
            name: 'hq:my:draft',
            component: () => import('@/views/headQuarter/MyDraftView.vue'),
            meta: { breadcrumb: '내 기안함' },
          },
          {
            path: 'approval',
            name: 'hq:my:approval',
            component: () => import('@/views/headQuarter/MyApprovalView.vue'),
            meta: { breadcrumb: '내 결재함' },
          },
        ],
      },
    ],
  },

  // 가맹점 화면
  {
    path: '/fc',
    name: 'fc:index',
    component: () => import('@/views/FranchiseIndexView.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: 'transaction',
        name: 'fc:home',
        component: () => import('@/components/franchise/FranchisePageBody.vue'),
        meta: {
          breadcrumb: '주문/교환/반품',
        },
        children: [
          {
            path: 'order/list',
            name: 'fc:home:order:list',
            component: () => import('@/views/franchise/OrderListView.vue'),
            meta: {
              breadcrumb: '주문 내역 조회',
            },
          },
          {
            path: 'order/:orderCode/detail',
            name: 'fc:home:order:detail',
            component: () => import('@/views/franchise/OrderDetailView.vue'),
            meta: {
              breadcrumb: '주문 상세',
              backTo: true,
            },
          },
          {
            path: 'order/form',
            name: 'fc:home:order:form',
            component: () => import('@/views/franchise/OrderFormView.vue'),
            meta: {
              breadcrumb: '주문하기',
            },
          },
          {
            path: 'exchange/list',
            name: 'fc:home:exchange:list',
            component: () => import('@/views/franchise/ExchangeListView.vue'),
            meta: {
              breadcrumb: '교환 내역 조회',
            },
          },
          {
            path: 'exchange/:exchangeCode/detail',
            name: 'fc:home:exchange:detail',
            component: () => import('@/views/franchise/ExchangeDetailView.vue'),
            meta: {
              breadcrumb: '교환 상세',
              backTo: true,
            },
          },
          {
            path: 'exchange/form',
            name: 'fc:home:exchange:form',
            component: () => import('@/views/franchise/ExchangeFormView.vue'),
            meta: {
              breadcrumb: '교환신청',
            },
          },
          {
            path: 'return/list',
            name: 'fc:home:return:list',
            component: () => import('@/views/franchise/ReturnListView.vue'),
            meta: {
              breadcrumb: '반품 내역 조회',
            },
          },
          {
            path: 'return/:returnCode/detail',
            name: 'fc:home:return:detail',
            component: () => import('@/views/franchise/ReturnDetailView.vue'),
            meta: {
              breadcrumb: '반품 상세',
              backTo: true,
            },
          },
          {
            path: 'return/form',
            name: 'fc:home:return:form',
            component: () => import('@/views/franchise/ReturnFormView.vue'),
            meta: {
              breadcrumb: '반품신청',
            },
          },
        ],
      },
      {
        path: 'other',
        name: 'fc:other',
        component: () => import('@/components/franchise/FranchisePageBody.vue'),
      },
    ],
  },

  // 배송기사 화면
  {
    path: '/d',
    name: 'd:index',
    component: () => import('@/views/DeliveryIndexView.vue'),
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: 'home',
        name: 'd:home',
        component: () => import('@/views/delivery/HomeView.vue'),
        meta: {
          pageTitle: '주문',
        },
      },
      {
        path: 'exchange-return',
        name: 'd:exchange-return',
        component: () => import('@/views/delivery/ExchangeReturnView.vue'),
        meta: {
          pageTitle: '교환/반품',
        },
      },
      {
        path: 'now-delivery',
        name: 'd:detail',
        component: () => import('@/views/delivery/DetailView.vue'),
        meta: {
          pageTitle: '배송 현황',
        },
      },
    ],
  },

  // 루트('/')로 접근 시 redirect
  {
    path: '/',
    name: 'index',
    redirect: { name: 'auth:login' },
  },

  { path: '/:pathMatch(.*)*', name: '404', component: () => import('@/views/NotFound.vue') },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: (to, from, savedPosition) => {
    if (savedPosition) return savedPosition;
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
      };
    }
    return { top: 0 };
  },
});

// 내비게이션 가드
router.beforeEach((to, from) => {
  const userStore = useUserStore();

  // 로그인이 완료된 유저가 guestOnly에 접근한 경우
  if (userStore.accessToken && to.meta.guestOnly) {
    if (userStore.userType === 'hq') {
      // 본사 계정이라면
      return {
        name: 'hq:home',
      };
    }

    if (userStore.userType === 'fc') {
      // 가맹점 계정이라면
      return {
        name: 'fc:home',
      };
    }

    if (userStore.userType === 'd') {
      // 배송 계정이라면
      return {
        name: 'd:home',
      };
    }
  }

  // 인증이 필요한 페이지인지 확인
  if (to.meta.requiresAuth && !userStore.accessToken) {
    return {
      name: 'auth:login',
    };
  }

  // 맞지 않은 유저가 방문했을 때 처리
  if (userStore.userType === 'hq' && !to.name.startsWith('hq:')) {
    return { name: 'hq:home' };
  }
  if (userStore.userType === 'fc' && !to.name.startsWith('fc:')) {
    return { name: 'fc:home' };
  }
  if (userStore.userType === 'd' && !to.name.startsWith('d:')) {
    return { name: 'd:home' };
  }

  if (to.name === 'auth:index') {
    return {
      name: 'auth:login',
    };
  }

  // 대메뉴 눌렀을 때 default 서브메뉴 선택 - 본사
  if (to.name === 'hq:order') {
    return {
      name: 'hq:order:list',
    };
  }

  if (to.name === 'hq:purchase') {
    return {
      name: 'hq:purchase:list',
    };
  }

  if (to.name === 'hq:settings') {
    return {
      name: 'hq:settings:employee:list',
    };
  }

  if (to.name === 'hq:partner') {
    return {
      name: 'hq:partner:franchise:list',
    };
  }

  if (to.name === 'hq:board') {
    return {
      name: 'hq:board:notice:list',
    };
  }

  if (to.name === 'hq:my') {
    return { name: 'hq:my:info' };
  }

  if (to.name === 'hq:stock') {
    return { name: 'hq:stock:storage-stock' };
  }

  // 대메뉴 눌렀을 때 default 서브메뉴 선택 - 가맹점
  if (to.name === 'fc:home') {
    return { name: 'fc:home:order:list' };
  }
});

export default router;
