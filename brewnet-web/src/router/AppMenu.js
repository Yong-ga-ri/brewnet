export default class AppMenu {
  #hqTopBarMenus = [
    this.#makeMenu('대시보드', 'hq:home'),
    this.#makeMenu('발주', 'hq:purchase'),
    this.#makeMenu('주문/교환/반품', 'hq:order'),
    this.#makeMenu('가맹점/거래처', 'hq:partner'),
    this.#makeMenu('창고/상품', 'hq:stock'),
    // this.#makeMenu('전자결재', 'hq:approval'),
    this.#makeMenu('게시판', 'hq:board'),
    this.#makeMenu('시스템설정', 'hq:settings'),
  ];
  #fcTopBarMenus = [
    this.#makeMenu('주문/교환/반품', 'fc:home'),
    // this.#makeMenu('기타사항', 'fc:other')
  ];

  #makeMenu(label, routerName) {
    return { label, routerName };
  }

  // TopBar 메뉴 가져오기
  getHqTopBarMenus() {
    return this.#hqTopBarMenus;
  }
  getFcTopBarMenu() {
    return this.#fcTopBarMenus;
  }

  // Side 메뉴 가져오기
  getSideMenus(currentRouteName = '') {
    // 본사
    if (currentRouteName.includes('hq:purchase')) {
      return [
        {
          label: '발주',
          items: [
            this.#makeMenu('발주 내역 조회', 'hq:purchase:list'),
            this.#makeMenu('발주 입력', 'hq:purchase:form'),
            this.#makeMenu('입고 확인', 'hq:purchase:stock-arrival'),
          ],
        },
      ];
    }

    if (currentRouteName.includes('hq:order')) {
      return [
        {
          label: '주문',
          items: [this.#makeMenu('주문요청 관리', 'hq:order:list')],
        },

        {
          label: '교환',
          items: [
            this.#makeMenu('교환요청 관리', 'hq:order:exchange:list'),
            this.#makeMenu('교환 처리내역 조회', 'hq:order:exchange:other-list'),
          ],
        },
        {
          label: '반품',
          items: [
            this.#makeMenu('반품요청 관리', 'hq:order:return:list'),
            this.#makeMenu('반품 처리내역 조회', 'hq:order:return:other-dept-stock'),
            this.#makeMenu('환불 처리내역 조회', 'hq:order:return:other-dept-refund'),
          ],
        },
      ];
    }

    if (currentRouteName.includes('hq:settings')) {
      return [
        {
          label: '임직원 관리',
          items: [
            this.#makeMenu('임직원 조회', 'hq:settings:employee:list'),
            this.#makeMenu('임직원 등록', 'hq:settings:employee:create'),
          ],
        },
        { label: '결재라인', items: [this.#makeMenu('결재라인 조회', 'hq:settings:approval-line')] },
        {
          label: '법인인감',
          items: [
            this.#makeMenu('법인인감 조회', 'hq:settings:company-seal'),
            this.#makeMenu('법인인감 등록', 'hq:settings:company-seal:upload'),
            this.#makeMenu('법인인감 사용내역', 'hq:settings:company-seal:history'),
          ],
        },
        { label: '기타', items: [this.#makeMenu('회사 정보', 'hq:settings:company')] },
      ];
    }

    if (currentRouteName.includes('hq:partner')) {
      return [
        {
          label: '가맹점',
          items: [
            this.#makeMenu('가맹점 조회', 'hq:partner:franchise:list'),
            this.#makeMenu('가맹점 등록', 'hq:partner:franchise:create'),
            this.#makeMenu('가맹점 계정 조회', 'hq:partner:franchise-account:list'),
            this.#makeMenu('가맹점 계정 등록', 'hq:partner:franchise-account:create'),
          ],
        },
        {
          label: '거래처',
          items: [
            this.#makeMenu('거래처 조회', 'hq:partner:correspondent:list'),
            this.#makeMenu('거래처 등록', 'hq:partner:correspondent:create'),
          ],
        },
      ];
    }

    if (currentRouteName.includes('hq:board')) {
      return [
        {
          label: '공지사항',
          items: [
            this.#makeMenu('공지사항 조회', 'hq:board:notice:list'),
            this.#makeMenu('공지사항 등록', 'hq:board:notice:create'),
          ],
        },
      ];
    }

    if (currentRouteName.includes('hq:my')) {
      return [
        {
          label: 'My',
          items: [
            this.#makeMenu('내 정보 조회', 'hq:my:info'),
            this.#makeMenu('비밀번호 변경', 'hq:my:change-password'),
            this.#makeMenu('서명 등록', 'hq:my:upload-signature'),
          ],
        },
        {
          label: 'My 문서',
          items: [this.#makeMenu('내 기안함', 'hq:my:draft'), this.#makeMenu('내 결재함', 'hq:my:approval')],
        },
      ];
    }

    if (currentRouteName.includes('hq:stock')) {
      return [
        {
          label: '창고',
          items: [
            this.#makeMenu('창고별 재고 조회', 'hq:stock:storage-stock'),
            this.#makeMenu('창고 목록', 'hq:stock:storage:list'),
            this.#makeMenu('창고 등록', 'hq:stock:storage:create'),
          ],
        },
        {
          label: '상품',
          items: [
            this.#makeMenu('상품 카테고리 관리', 'hq:stock:item-category'),
            this.#makeMenu('상품 조회', 'hq:stock:item:list'),
            this.#makeMenu('상품 등록', 'hq:stock:item:create'),
            this.#makeMenu('필수구매상품 관리', 'hq:stock:item-must-buy:list'),
          ],
        },
      ];
    }

    // 가맹점
    if (currentRouteName.includes('fc:home')) {
      return [
        {
          label: '주문관리',
          items: [
            this.#makeMenu('주문 내역 조회', 'fc:home:order:list'),
            this.#makeMenu('주문하기', 'fc:home:order:form'),
          ],
        },
        {
          label: '교환관리',
          items: [
            this.#makeMenu('교환 내역 조회', 'fc:home:exchange:list'),
            this.#makeMenu('교환신청', 'fc:home:exchange:form'),
          ],
        },
        {
          label: '반품관리',
          items: [
            this.#makeMenu('반품 내역 조회', 'fc:home:return:list'),
            this.#makeMenu('반품신청', 'fc:home:return:form'),
          ],
        },
      ];
    }
  }
}
