// 임직원
function makeMockEmployee(code, name, id, email, contact, position, role) {
  return { code, name, id, email, contact, position, role };
}
export const mockupEmployees = [
  makeMockEmployee(100, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(101, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(102, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(103, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(104, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(105, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(106, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(107, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(108, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(109, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_MASTER'),
  makeMockEmployee(110, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(111, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(112, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'STAFF', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(113, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(114, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(115, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(116, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(117, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(118, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(119, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(120, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(121, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_GENERAL_ADMIN'),
  makeMockEmployee(122, '홍길동', 'BN001', 'gdhong@bn.com', '010-1111-1111', 'MANAGER', 'ROLE_RESPONSIBLE_ADMIN'),
];

// 가맹점 계정
function makeMockFranchiseAccount(code, id, email, contact, franchiseName) {
  return { code, id, email, contact, franchiseName };
}
export const mockupFranchiseAccounts = [
  makeMockFranchiseAccount(100, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안양점'),
  makeMockFranchiseAccount(101, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(102, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(103, 'BN001', 'gdhong@bn.com', '010-1111-1111', '강북점'),
  makeMockFranchiseAccount(104, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(105, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(106, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(107, 'BN001', 'gdhong@bn.com', '010-1111-1111', '미아점'),
  makeMockFranchiseAccount(108, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(109, 'BN001', 'gdhong@bn.com', '010-1111-1111', '안산점'),
  makeMockFranchiseAccount(122, 'BN001', 'gdhong@bn.com', '010-1111-1111', '종암점'),
];

// 가맹점
function makeMockFranchise(code, franchiseName, city, address, detailAddress, businessNumber, ceo, contact, createdAt) {
  return { code, franchiseName, city, address, detailAddress, businessNumber, ceo, contact, createdAt };
}

export const mockupFranchises = [
  makeMockFranchise(
    1,
    'CoffeeHouse A',
    '서울',
    '123 Main St',
    '111번지',
    '111-11-11111',
    '노다민',
    '010-1234-5678',
    '2024-01-01 08:00:00',
  ),
  makeMockFranchise(
    2,
    'CoffeeHouse B',
    '부산',
    '456 Maple St',
    '222번지',
    '111-11-22222',
    '양현진',
    '010-2345-6789',
    '2024-01-02 09:00:00',
  ),
  makeMockFranchise(
    3,
    'CoffeeHouse C',
    '인천',
    '789 Oak St',
    '333번지',
    '111-11-33333',
    '용길한',
    '010-3456-7890',
    '2024-01-03 10:00:00',
  ),
  makeMockFranchise(
    4,
    'CoffeeHouse D',
    '대구',
    '101 Pine St',
    '444번지',
    '111-11-44444',
    '장호정',
    '010-4567-8901',
    '2024-01-04 11:00:00',
  ),
  makeMockFranchise(
    5,
    'CoffeeHouse E',
    '전라남도',
    '202 Cedar St',
    '555번지',
    '111-11-55555',
    '정준서',
    '010-5678-9012',
    '2024-01-05 12:00:00',
  ),
];

// 법인인감 사용내역
function makeMockCompanySealHistory(code, name, reason, useDate) {
  return { code, name, reason, useDate };
}
export const mockupCompanySealHistory = [
  makeMockCompanySealHistory(100, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(101, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(102, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(103, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(104, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(105, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(106, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(107, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(108, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(109, '홍길동', '발주서 출력을 위해 사용됨', '2024/10/21'),
  makeMockCompanySealHistory(110, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/11'),
  makeMockCompanySealHistory(111, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/11'),
  makeMockCompanySealHistory(112, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/11'),
  makeMockCompanySealHistory(113, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(114, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(115, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(116, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(117, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(118, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(119, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(120, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(121, '홍길동', '발주서 출력을 위해 사용됨', '2024/09/09'),
  makeMockCompanySealHistory(122, '홍길동', '발주서 출력을 위해 사용됨', '2024/01/11'),
];

// 공지사항
function makeMockNotice(code, title, content, createdAt, author, images) {
  return { code, title, content, createdAt, author, images };
}

export const mockupNotices = [
  makeMockNotice(1, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', [
    'https://primefaces.org/cdn/primevue/images/galleria/galleria10.jpg',
    'https://primefaces.org/cdn/primevue/images/galleria/galleria10.jpg',
  ]),
  makeMockNotice(2, '주문 시 공지사항 업데이트', '내용', '2024-11-10 11:11:11', '홍길동', []),
  makeMockNotice(3, '주문 시 공지사항 업데이트', '내용', '2024-11-09 11:11:11', '홍길동', []),
  makeMockNotice(4, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(5, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(6, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(7, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(8, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(9, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(10, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(11, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(12, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
  makeMockNotice(13, '주문 시 공지사항 업데이트', '내용', '2024-11-11 11:11:11', '홍길동', []),
];

// 교환
function makeMockExchange(exchangeCode, franchiseName, itemName, reason, memberCode, createdAt, status, approved) {
  return { exchangeCode, franchiseName, itemName, reason, memberCode, createdAt, status, approved };
}
export const mockupExchanges = [
  makeMockExchange(
    '1',
    '메가커피 신대방삼거리점',
    '빨대(100개입)',
    'DAMAGED',
    '김사원',
    '2024-11-01',
    'APPROVED',
    'APPROVE',
  ),
  makeMockExchange(
    '2',
    '메가커피 안양점',
    '종이컵(S), 플라스틱컵(M)',
    'DEFECTIVE',
    '이사원',
    '2024-11-02',
    'REJECTED',
    'REJECT',
  ),
  makeMockExchange(
    '3',
    '메가커피 서울역점',
    '블랜드원두(3kg), 바닐라파우더(5kg), 휘핑크림(500g)',
    'OTHER',
    null,
    '2024-11-03',
    'REQUESTED',
    'NONE',
  ),
  makeMockExchange(
    '4',
    '메가커피 강남역점',
    '블랜드원두 미디엄(3kg)',
    'DAMAGED',
    null,
    '2024-11-04',
    'REQUESTED',
    'NONE',
  ),
  makeMockExchange(
    '5',
    '메가커피 홍대점',
    '종이컵(S), 종이컵(M)',
    'DEFECTIVE',
    '박사원',
    '2024-11-05',
    'APPROVED',
    'APPROVE',
  ),
  makeMockExchange(
    '6',
    '메가커피 신림점',
    '빨대(100개입), 플라스틱컵(M), 플라스틱컵(L)',
    'OTHER',
    '최사원',
    '2024-11-06',
    'REJECTED',
    'REJECT',
  ),
  makeMockExchange(
    '7',
    '메가커피 종각점',
    '블랜드원두(3kg), 망고스무디베이스(1kg)',
    'DAMAGED',
    null,
    '2024-11-07',
    'REQUESTED',
    'NONE',
  ),
  makeMockExchange(
    '8',
    '메가커피 압구정점',
    '블랜드원두 미디엄(3kg)',
    'DEFECTIVE',
    '정사원',
    '2024-11-08',
    'APPROVED',
    'APPROVE',
  ),
  makeMockExchange(
    '9',
    '메가커피 서초점',
    '빨대(100개입), 스틱빨대(100개입)',
    'OTHER',
    null,
    '2024-11-09',
    'REQUESTED',
    'NONE',
  ),
  makeMockExchange(
    '10',
    '메가커피 건대점',
    '종이컵(S), 레몬에이드(1kg)',
    'DEFECTIVE',
    '한사원',
    '2024-11-10',
    'REJECTED',
    'REJECT',
  ),
  makeMockExchange(
    '11',
    '메가커피 잠실점',
    '블랜드원두(3kg)',
    'DAMAGED',
    '서사원',
    '2024-11-11',
    'APPROVED',
    'APPROVE',
  ),
  makeMockExchange(
    '12',
    '메가커피 마포점',
    '빨대(100개입), 종이컵(S)',
    'DEFECTIVE',
    null,
    '2024-11-12',
    'REQUESTED',
    'NONE',
  ),
  makeMockExchange(
    '13',
    '메가커피 여의도점',
    '블랜드원두 미디엄(3kg), 시럽(500ml)',
    'OTHER',
    '유사원',
    '2024-11-13',
    'REJECTED',
    'REJECT',
  ),
  makeMockExchange(
    '14',
    '메가커피 역삼점',
    '빨대(100개입), 얼음컵(L)',
    'DEFECTIVE',
    '홍사원',
    '2024-11-14',
    'APPROVED',
    'APPROVE',
  ),
  makeMockExchange('15', '메가커피 성수점', '얼음컵(L), 종이컵(M)', 'DAMAGED', null, '2024-11-15', 'REQUESTED', 'NONE'),
];

// 주문
function makeMockOrder(
  orderCode,
  franchiseName,
  itemName,
  sumPrice,
  approvalStatus,
  drafterApproved,
  managerName,
  createdAt,
) {
  return { orderCode, franchiseName, itemName, sumPrice, approvalStatus, drafterApproved, managerName, createdAt };
}
export const mockupOrders = [
  makeMockOrder(10, '메가커피 신대방삼거리점', '종이컵(S), 종이컵(M)', 12000, 'REQUESTED', 'NONE', null, '2024-11-10'),
  makeMockOrder(
    9,
    '메가커피 안양점',
    '헤이즐넛시럽(1L), 바닐라시럽(1L)',
    25000,
    'PENDING',
    'REJECT',
    '담당자 B',
    '2024-11-09',
  ),
  makeMockOrder(8, '메가커피 강남역점', '아이스컵(L), 빨대', 15000, 'APPROVED', 'APPROVE', '담당자 C', '2024-11-08'),
  makeMockOrder(
    7,
    '메가커피 신림점',
    '카라멜시럽(1L), 에스프레소빈(1kg)',
    30000,
    'REJECTED',
    'REJECT',
    '담당자 D',
    '2024-11-07',
  ),
  makeMockOrder(6, '메가커피 종각역점', '우유(1L), 초코파우더', 8000, 'CANCELED', 'NONE', null, '2024-11-06'),
  makeMockOrder(5, '메가커피 혜화점', '컵홀더, 종이컵(L)', 18000, 'SHIPPING', 'APPROVE', '담당자 F', '2024-11-05'),
  makeMockOrder(4, '메가커피 이대점', '자몽시럽(1L), 레몬청(1kg)', 22000, 'REQUESTED', 'NONE', null, '2024-11-04'),
  makeMockOrder(3, '메가커피 역삼점', '얼음컵(M), 컵 뚜껑', 10000, 'PENDING', 'APPROVE', '담당자 H', '2024-11-03'),
  makeMockOrder(
    2,
    '메가커피 홍대입구점',
    '설탕(1kg), 시나몬파우더',
    14000,
    'REJECTED',
    'REJECT',
    '담당자 I',
    '2024-11-02',
  ),
  makeMockOrder(
    1,
    '메가커피 연신내점',
    '티백(녹차), 플라스틱 스푼',
    16000,
    'APPROVED',
    'APPROVE',
    '담당자 J',
    '2024-11-01',
  ),
];

// 거래처
function makeMockSupplier(code, name, managerName, address, detailAddress, contact, email) {
  return { code, name, managerName, address, detailAddress, contact, email };
}

export const mockupSuppliers = [
  makeMockSupplier(
    99,
    '원두A공장',
    '홍길동',
    '서울 관악구 난곡로 367',
    '상세주소',
    '010-1111-1111',
    'gdhong@company.com',
  ),
  makeMockSupplier(
    98,
    '원두B공장',
    '용길한',
    '서울 관악구 난곡로 367',
    '상세주소',
    '010-1111-1111',
    'gdhong@company.com',
  ),
  makeMockSupplier(
    97,
    '원두C공장',
    '정준서',
    '서울 관악구 난곡로 367',
    '상세주소',
    '010-1111-1111',
    'gdhong@company.com',
  ),
];

// 상품
function makeMockItem(code, name, purchasePrice, sellingPrice, imageUrl, safetyStock, uniqueCode, category) {
  return { code, name, purchasePrice, sellingPrice, imageUrl, safetyStock, uniqueCode, category };
}
export const mockupItems = [
  makeMockItem(
    1,
    '산토스',
    20000,
    30000,
    'https://media.istockphoto.com/id/2158812697/ko/%EC%82%AC%EC%A7%84/%EC%B0%BD%EA%B0%80-%EC%98%86-%EB%82%98%EB%AC%B4-%ED%83%81%EC%9E%90-%EC%9C%84%EC%97%90-%EB%86%93%EC%9D%B8-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC-%ED%95%9C-%EC%9E%94%EC%9D%98-%ED%95%98%EC%9D%B4-%EC%95%B5%EA%B8%80-%EB%B7%B0.jpg?s=2048x2048&w=is&k=20&c=_qrZuDASfDBr70l-C0I6wLs03vRwZhlBkyx3d4nSUc4=',
    100,
    'C10',
    '원두',
  ),
  makeMockItem(
    2,
    '산토스 디카페인',
    20000,
    30000,
    'https://media.istockphoto.com/id/2158812697/ko/%EC%82%AC%EC%A7%84/%EC%B0%BD%EA%B0%80-%EC%98%86-%EB%82%98%EB%AC%B4-%ED%83%81%EC%9E%90-%EC%9C%84%EC%97%90-%EB%86%93%EC%9D%B8-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC-%ED%95%9C-%EC%9E%94%EC%9D%98-%ED%95%98%EC%9D%B4-%EC%95%B5%EA%B8%80-%EB%B7%B0.jpg?s=2048x2048&w=is&k=20&c=_qrZuDASfDBr70l-C0I6wLs03vRwZhlBkyx3d4nSUc4=',
    100,
    'C101',
    '원두',
  ),
  makeMockItem(
    3,
    '슈프리모',
    20000,
    30000,
    'https://media.istockphoto.com/id/2158812697/ko/%EC%82%AC%EC%A7%84/%EC%B0%BD%EA%B0%80-%EC%98%86-%EB%82%98%EB%AC%B4-%ED%83%81%EC%9E%90-%EC%9C%84%EC%97%90-%EB%86%93%EC%9D%B8-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC-%ED%95%9C-%EC%9E%94%EC%9D%98-%ED%95%98%EC%9D%B4-%EC%95%B5%EA%B8%80-%EB%B7%B0.jpg?s=2048x2048&w=is&k=20&c=_qrZuDASfDBr70l-C0I6wLs03vRwZhlBkyx3d4nSUc4=',
    100,
    'C111',
    '원두',
  ),
];

// 창고
function makeMockStorage(code, name, address, contact) {
  return { code, name, address, contact };
}
export const mockupStorages = [
  makeMockStorage(1, '센트럴 창고', '세종특별자치시 한누리대로 2130', '044-234-5678'),
  makeMockStorage(2, '동부 창고', '세종특별자치시 한누리대로 2130', '044-234-5678'),
  makeMockStorage(3, '서부 창고', '세종특별자치시 한누리대로 2130', '044-234-5678'),
  makeMockStorage(4, '남부 창고', '세종특별자치시 한누리대로 2130', '044-234-5678'),
];

// 발주
function makeMockPurchase(
  status,
  code,
  supplierName,
  totalPrice,
  storageName,
  username,
  createdAt,
  approvalAt,
  comment,
) {
  return { status, code, supplierName, totalPrice, storageName, username, createdAt, approvalAt, comment };
}
export const mockupPurchases = [
  makeMockPurchase(
    'REQUESTED',
    100,
    '원두공장A',
    100000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
  makeMockPurchase(
    'APPROVED',
    99,
    '원두공장A',
    200000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
  makeMockPurchase(
    'APPROVED',
    98,
    '원두공장A',
    300000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
  makeMockPurchase(
    'CANCELED',
    97,
    '원두공장A',
    400000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
  makeMockPurchase(
    'REJECTED',
    96,
    '원두공장A',
    500000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
  makeMockPurchase(
    'REQUESTED',
    95,
    '원두공장A',
    600000,
    '서부창고',
    '홍길동',
    '2024-11-11',
    '2024-11-11',
    '발주에 대한 비고사항입니다~ 참고해주세요',
  ),
];

function makeMockItemCategory(categoryCode, categoryName) {
  return { categoryCode, categoryName };
}

export const mockupNonFoodItemCategories = [
  makeMockItemCategory(100, '머신'),
  makeMockItemCategory(101, '세제'),
  makeMockItemCategory(102, '일회용기'),
];

export const mockupFoodItemCategories = [
  makeMockItemCategory(200, '원두'),
  makeMockItemCategory(201, '냉동식품'),
  makeMockItemCategory(202, '시럽'),
];
