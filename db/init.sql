-- init.sql (mariadb/init.sql)
CREATE DATABASE IF NOT EXISTS brewnet;

CREATE USER IF NOT EXISTS 'varc'@'%' IDENTIFIED BY 'varcpw';

GRANT ALL PRIVILEGES ON brewnet.* TO 'varc'@'%';

FLUSH PRIVILEGES;

USE brewnet;

DROP TABLE IF EXISTS tbl_company;
CREATE TABLE tbl_company (
  company_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  business_number varchar(255) NOT NULL,
  corporate_number varchar(255) DEFAULT NULL,
  ceo_name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  type_of_business varchar(255) NOT NULL,
  contact varchar(255) NOT NULL,
  date_of_establishment varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (company_code),
  UNIQUE KEY company_code (company_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_correspondent;
CREATE TABLE tbl_correspondent (
  correspondent_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  detail_address varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  contact varchar(255) DEFAULT NULL,
  manager_name varchar(255) DEFAULT NULL,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  PRIMARY KEY (correspondent_code),
  UNIQUE KEY correspondent_code (correspondent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_franchise;
CREATE TABLE tbl_franchise (
  franchise_code int(11) NOT NULL AUTO_INCREMENT,
  franchise_name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  detail_address varchar(255) NOT NULL,
  city varchar(255) NOT NULL,
  contact varchar(255) NOT NULL,
  business_number varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (franchise_code),
  UNIQUE KEY franchise_code (franchise_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_position;
CREATE TABLE tbl_position (
  position_code int(11) NOT NULL AUTO_INCREMENT,
  name enum('STAFF','ASSISTANT_MANAGER','MANAGER','CEO') NOT NULL COMMENT '사원, 대리, 과장, 대표이사',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (position_code),
  UNIQUE KEY position_code (position_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_approval;
CREATE TABLE tbl_approval (
  approval_code int(11) NOT NULL AUTO_INCREMENT,
  kind enum('PURCHASE','ORDER','EXCHANGE','RETURN') NOT NULL,
  sequence int(11) NOT NULL COMMENT '현재는 기안자 1명, 결재자 1명으로 순번은 1고정',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  position_code int(11) NOT NULL,
  PRIMARY KEY (approval_code),
  UNIQUE KEY approval_code (approval_code),
  KEY FK_tbl_position_TO_tbl_approval_1 (position_code),
  CONSTRAINT FK_tbl_position_TO_tbl_approval_1 FOREIGN KEY (position_code) REFERENCES tbl_position (position_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_member;
CREATE TABLE tbl_member (
  member_code int(11) NOT NULL AUTO_INCREMENT,
  id varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  contact varchar(255) NOT NULL,
  signature_url varchar(500) DEFAULT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  position_code int(11) DEFAULT NULL,
  PRIMARY KEY (member_code),
  UNIQUE KEY member_code (member_code),
  KEY FK_tbl_position_TO_tbl_member_1 (position_code),
  CONSTRAINT FK_tbl_position_TO_tbl_member_1 FOREIGN KEY (position_code) REFERENCES tbl_position (position_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_franchise_member;
CREATE TABLE tbl_franchise_member (
  franchise_member_code int(11) NOT NULL AUTO_INCREMENT,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  franchise_code int(11) NOT NULL,
  PRIMARY KEY (franchise_member_code),
  UNIQUE KEY franchise_member_code (franchise_member_code),
  KEY FK_tbl_franchise_TO_tbl_franchise_member_1 (franchise_code),
  KEY FK_tbl_member_TO_tbl_franchise_member_1 (member_code),
  CONSTRAINT FK_tbl_franchise_TO_tbl_franchise_member_1 FOREIGN KEY (franchise_code) REFERENCES tbl_franchise (franchise_code),
  CONSTRAINT FK_tbl_member_TO_tbl_franchise_member_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_notice;
CREATE TABLE tbl_notice (
  notice_code int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) NOT NULL,
  content varchar(1024) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (notice_code),
  UNIQUE KEY notice_code (notice_code),
  KEY FK_tbl_member_TO_tbl_notice_1 (member_code),
  CONSTRAINT FK_tbl_member_TO_tbl_notice_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_notice_img;
CREATE TABLE tbl_notice_img (
  notice_img_code int(11) NOT NULL AUTO_INCREMENT,
  image_url varchar(500) NOT NULL,
  notice_code int(11) NOT NULL,
  PRIMARY KEY (notice_img_code),
  UNIQUE KEY notice_img_code (notice_img_code),
  KEY FK_tbl_notice_TO_tbl_notice_img_1 (notice_code),
  CONSTRAINT FK_tbl_notice_TO_tbl_notice_img_1 FOREIGN KEY (notice_code) REFERENCES tbl_notice (notice_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_order;
CREATE TABLE tbl_order (
  order_code int(11) NOT NULL AUTO_INCREMENT,
  comment varchar(255) DEFAULT NULL COMMENT '누가 작성하는가? -> 주문담당자',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  approval_status enum('APPROVED','CANCELED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '결재 확인되지 않음, 결재 취소, 결재 승인, 결재 반려',
  drafter_approved enum('APPROVE','REJECT','NONE') NOT NULL COMMENT '승인, 반려, 미정',
  sum_price int(11) NOT NULL,
  franchise_code int(11) NOT NULL,
  member_code int(11) DEFAULT NULL,
  delivery_code int(11) DEFAULT NULL,
  PRIMARY KEY (order_code),
  UNIQUE KEY order_code (order_code),
  KEY FK_tbl_franchise_TO_tbl_order_1 (franchise_code),
  KEY FK_tbl_member_TO_tbl_order_1 (member_code),
  KEY FK_tbl_member_TO_tbl_order_2 (delivery_code),
  CONSTRAINT FK_tbl_franchise_TO_tbl_order_1 FOREIGN KEY (franchise_code) REFERENCES tbl_franchise (franchise_code),
  CONSTRAINT FK_tbl_member_TO_tbl_order_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_member_TO_tbl_order_2 FOREIGN KEY (delivery_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;


DROP TABLE IF EXISTS tbl_exchange;
CREATE TABLE tbl_exchange (
  exchange_code int(11) NOT NULL AUTO_INCREMENT,
  comment varchar(255) DEFAULT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  reason enum('DAMAGED','DEFECTIVE','OTHER') NOT NULL COMMENT '파손,품질불량,기타',
  explanation varchar(255) NOT NULL,
  approval_status enum('APPROVED','CANCELED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '결재 확인되지 않음, 결재 취소, 결재 승인, 결재 반려',
  order_code int(11) NOT NULL,
  member_code int(11) DEFAULT NULL,
  delivery_code int(11) DEFAULT NULL,
  drafter_approved enum('APPROVE','REJECT','NONE') NOT NULL COMMENT '승인, 반려, 미정',
  sum_price int(11) NOT NULL,
  PRIMARY KEY (exchange_code),
  UNIQUE KEY exchange_code (exchange_code),
  KEY FK_tbl_member_TO_tbl_exchange_1 (member_code),
  KEY FK_tbl_member_TO_tbl_exchange_2 (delivery_code),
  KEY FK_tbl_order_TO_tbl_exchange_1 (order_code),
  CONSTRAINT FK_tbl_member_TO_tbl_exchange_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_member_TO_tbl_exchange_2 FOREIGN KEY (delivery_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_order_TO_tbl_exchange_1 FOREIGN KEY (order_code) REFERENCES tbl_order (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_approver;
CREATE TABLE tbl_exchange_approver (
  member_code int(11) NOT NULL,
  exchange_code int(11) NOT NULL,
  approved enum('APPROVED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '승인, 미확인, 반려',
  created_at datetime DEFAULT NULL,
  updated_at datetime DEFAULT NULL,
  comment varchar(255) DEFAULT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,exchange_code),
  KEY FK_tbl_exchange_TO_tbl_exchange_approver_1 (exchange_code),
  CONSTRAINT FK_tbl_exchange_TO_tbl_exchange_approver_1 FOREIGN KEY (exchange_code) REFERENCES tbl_exchange (exchange_code),
  CONSTRAINT FK_tbl_member_TO_tbl_exchange_approver_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_img;
CREATE TABLE tbl_exchange_img (
  exchange_img_code int(11) NOT NULL AUTO_INCREMENT,
  image_url varchar(500) NOT NULL,
  exchange_code int(11) NOT NULL,
  PRIMARY KEY (exchange_img_code),
  UNIQUE KEY exchange_img_code (exchange_img_code),
  KEY FK_tbl_exchange_TO_tbl_exchange_img_1 (exchange_code),
  CONSTRAINT FK_tbl_exchange_TO_tbl_exchange_img_1 FOREIGN KEY (exchange_code) REFERENCES tbl_exchange (exchange_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_status_history;
CREATE TABLE tbl_exchange_status_history (
  exchange_status_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('REQUESTED','PENDING','CANCELED','APPROVED','REJECTED','PICKING','PICKED','SHIPPING','SHIPPED','COMPLETED') NOT NULL COMMENT '교환요청, 진행중, 교환취소, 교환승인, 교환 반려, 수거중, 수거완료, 배송중, 배송완료,교환완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  exchange_code int(11) NOT NULL,
  PRIMARY KEY (exchange_status_history_code),
  UNIQUE KEY exchange_status_history_code (exchange_status_history_code),
  KEY FK_tbl_exchange_TO_tbl_exchange_status_history_1 (exchange_code),
  CONSTRAINT FK_tbl_exchange_TO_tbl_exchange_status_history_1 FOREIGN KEY (exchange_code) REFERENCES tbl_exchange (exchange_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_stock_history;
CREATE TABLE tbl_exchange_stock_history (
  exchange_stock_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('TOTAL_INBOUND','TOTAL_DISPOSAL','PARTIAL_INBOUND') NOT NULL COMMENT '전체입고,전체폐기, 부분입고',
  manager varchar(255) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  confirmed enum('CONFIRMED','UNCONFIRMED') NOT NULL COMMENT '미처리, 처리완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  exchange_code int(11) NOT NULL,
  PRIMARY KEY (exchange_stock_history_code),
  UNIQUE KEY exchange_stock_history_code (exchange_stock_history_code),
  KEY FK_tbl_exchange_TO_tbl_exchange_stock_history_1 (exchange_code),
  CONSTRAINT FK_tbl_exchange_TO_tbl_exchange_stock_history_1 FOREIGN KEY (exchange_code) REFERENCES tbl_exchange (exchange_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_order_approver;
CREATE TABLE tbl_order_approver (
  member_code int(11) NOT NULL,
  order_code int(11) NOT NULL,
  approved enum('APPROVED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '승인, 미확인, 반려',
  created_at datetime DEFAULT NULL,
  updated_at datetime DEFAULT NULL,
  comment varchar(255) DEFAULT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,order_code),
  KEY FK_tbl_order_TO_tbl_order_approver_1 (order_code),
  CONSTRAINT FK_tbl_member_TO_tbl_order_approver_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_order_TO_tbl_order_approver_1 FOREIGN KEY (order_code) REFERENCES tbl_order (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_order_status_history;
CREATE TABLE tbl_order_status_history (
  order_status_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('REQUESTED','PENDING','CANCELED','APPROVED','REJECTED','SHIPPING','SHIPPED') NOT NULL COMMENT '주문요청, 진행중, 주문취소, 주문승인,  주문 반려, 배송중, 배송완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  order_code int(11) NOT NULL,
  PRIMARY KEY (order_status_history_code),
  UNIQUE KEY order_status_history_code (order_status_history_code),
  KEY FK_tbl_order_TO_tbl_order_status_history_1 (order_code),
  CONSTRAINT FK_tbl_order_TO_tbl_order_status_history_1 FOREIGN KEY (order_code) REFERENCES tbl_order (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return;
CREATE TABLE tbl_return (
  return_code int(11) NOT NULL AUTO_INCREMENT,
  comment varchar(255) DEFAULT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  reason enum('DAMAGED','DEFECTIVE','MIND_CHANGE','OTHER') NOT NULL COMMENT '파손, 품질불량, 단순변심, 기타',
  explanation varchar(255) NOT NULL,
  approval_status enum('APPROVED','CANCELED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '결재 확인되지 않음, 결재 취소, 결재 승인, 결재 반려',
  order_code int(11) NOT NULL,
  member_code int(11) DEFAULT NULL,
  delivery_code int(11) DEFAULT NULL,
  drafter_approved enum('APPROVE','REJECT','NONE') NOT NULL COMMENT '승인, 반려, 미정',
  sum_price int(11) NOT NULL,
  PRIMARY KEY (return_code),
  UNIQUE KEY return_code (return_code),
  KEY FK_tbl_member_TO_tbl_return_1 (member_code),
  KEY FK_tbl_member_TO_tbl_return_2 (delivery_code),
  KEY FK_tbl_order_TO_tbl_return_1 (order_code),
  CONSTRAINT FK_tbl_member_TO_tbl_return_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_member_TO_tbl_return_2 FOREIGN KEY (delivery_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_order_TO_tbl_return_1 FOREIGN KEY (order_code) REFERENCES tbl_order (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_approver;
CREATE TABLE tbl_return_approver (
  member_code int(11) NOT NULL,
  return_code int(11) NOT NULL,
  approved enum('APPROVED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '승인, 미확인, 반려',
  created_at datetime DEFAULT NULL,
  updated_at datetime DEFAULT NULL,
  comment varchar(255) DEFAULT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,return_code),
  KEY FK_tbl_return_TO_tbl_return_approver_1 (return_code),
  CONSTRAINT FK_tbl_member_TO_tbl_return_approver_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_approver_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_img;
CREATE TABLE tbl_return_img (
  return_img_code int(11) NOT NULL AUTO_INCREMENT,
  imag_url varchar(500) NOT NULL,
  return_code int(11) NOT NULL,
  PRIMARY KEY (return_img_code),
  UNIQUE KEY return_img_code (return_img_code),
  KEY FK_tbl_return_TO_tbl_return_img_1 (return_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_img_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_refund_history;
CREATE TABLE tbl_return_refund_history (
  return_refund_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('TOTAL_REFUND','PARTIAL_REFUND','NON_REFUNDABLE') NOT NULL COMMENT '전체환불, 부분환불, 환불불가',
  manager varchar(255) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  confirmed enum('CONFIRMED','UNCONFIRMED') NOT NULL COMMENT '미처리, 처리완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  return_code int(11) NOT NULL,
  PRIMARY KEY (return_refund_history_code),
  UNIQUE KEY return_refund_history_code (return_refund_history_code),
  KEY FK_tbl_return_TO_tbl_return_refund_history_1 (return_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_refund_history_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_status_history;
CREATE TABLE tbl_return_status_history (
  return_status_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('REQUESTED','PENDING','CANCELED','APPROVED','REJECTED','PICKING','PICKED','COMPLETED') NOT NULL COMMENT '반품요청, 진행중, 반품취소, 반품승인, 반품반려, 수거중, 수거완료, 반품완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  return_code int(11) NOT NULL,
  PRIMARY KEY (return_status_history_code),
  UNIQUE KEY return_status_history_code (return_status_history_code),
  KEY FK_tbl_return_TO_tbl_return_status_history_1 (return_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_status_history_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_stock_history;
CREATE TABLE tbl_return_stock_history (
  return_stock_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('TOTAL_INBOUND','PARTIAL_INBOUND','TOTAL_DISPOSAL') NOT NULL COMMENT '전체입고, 부분입고, 전체폐기',
  manager varchar(255) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  confirmed enum('CONFIRMED','UNCONFIRMED') NOT NULL COMMENT '미처리, 처리완료',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  return_code int(11) NOT NULL,
  PRIMARY KEY (return_stock_history_code),
  UNIQUE KEY return_stock_history_code (return_stock_history_code),
  KEY FK_tbl_return_TO_tbl_return_stock_history_1 (return_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_stock_history_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_role;
CREATE TABLE tbl_role (
  role_code int(11) NOT NULL AUTO_INCREMENT,
  role enum('ROLE_MASTER','ROLE_GENERAL_ADMIN','ROLE_RESPONSIBLE_ADMIN','ROLE_FRANCHISE','ROLE_DELIVERY') NOT NULL COMMENT '마스터, 일반 관리자, 책임 관리자, 가맹점, 배송기사)',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (role_code),
  UNIQUE KEY role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_member_role;
CREATE TABLE tbl_member_role (
  role_code int(11) NOT NULL,
  member_code int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (role_code,member_code),
  KEY FK_tbl_member_TO_tbl_member_role_1 (member_code),
  CONSTRAINT FK_tbl_member_TO_tbl_member_role_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_role_TO_tbl_member_role_1 FOREIGN KEY (role_code) REFERENCES tbl_role (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_seal;
CREATE TABLE tbl_seal (
  seal_code int(11) NOT NULL AUTO_INCREMENT,
  image_url varchar(500) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  company_code int(11) NOT NULL,
  PRIMARY KEY (seal_code),
  UNIQUE KEY seal_code (seal_code),
  KEY FK_tbl_company_TO_tbl_seal_1 (company_code),
  CONSTRAINT FK_tbl_company_TO_tbl_seal_1 FOREIGN KEY (company_code) REFERENCES tbl_company (company_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_storage;
CREATE TABLE tbl_storage (
  storage_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  address varchar(255) NOT NULL,
  contact varchar(255) NOT NULL,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  PRIMARY KEY (storage_code),
  UNIQUE KEY storage_code (storage_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_letter_of_purchase;
CREATE TABLE tbl_letter_of_purchase (
  letter_of_purchase_code int(11) NOT NULL AUTO_INCREMENT,
  comment varchar(255) DEFAULT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  approved enum('APPROVED','UNCONFIRMED','REJECTED') NOT NULL,
  sum_price int(11) NOT NULL,
  correspondent_code int(11) NOT NULL,
  member_code int(11) NOT NULL,
  storage_code int(11) NOT NULL,
  seal_code int(11) DEFAULT NULL,
  PRIMARY KEY (letter_of_purchase_code),
  UNIQUE KEY letter_of_purchase_code (letter_of_purchase_code),
  KEY FK_tbl_correspondent_TO_tbl_letter_of_purchase_1 (correspondent_code),
  KEY FK_tbl_member_TO_tbl_letter_of_purchase_1 (member_code),
  KEY FK_tbl_seal_TO_tbl_letter_of_purchase_1 (seal_code),
  KEY FK_tbl_storage_TO_tbl_letter_of_purchase_1 (storage_code),
  CONSTRAINT FK_tbl_correspondent_TO_tbl_letter_of_purchase_1 FOREIGN KEY (correspondent_code) REFERENCES tbl_correspondent (correspondent_code),
  CONSTRAINT FK_tbl_member_TO_tbl_letter_of_purchase_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code),
  CONSTRAINT FK_tbl_seal_TO_tbl_letter_of_purchase_1 FOREIGN KEY (seal_code) REFERENCES tbl_seal (seal_code),
  CONSTRAINT FK_tbl_storage_TO_tbl_letter_of_purchase_1 FOREIGN KEY (storage_code) REFERENCES tbl_storage (storage_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_letter_of_purchase_approver;
CREATE TABLE tbl_letter_of_purchase_approver (
  member_code int(11) NOT NULL,
  letter_of_purchase_code int(11) NOT NULL,
  approved enum('APPROVED','UNCONFIRMED','REJECTED') NOT NULL COMMENT '승인, 미확인, 반려',
  approved_at datetime DEFAULT NULL,
  active tinyint(1) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  PRIMARY KEY (member_code,letter_of_purchase_code),
  KEY FK_tbl_letter_of_purchase_TO_tbl_letter_of_purchase_approver_1 (letter_of_purchase_code),
  CONSTRAINT FK_tbl_letter_of_purchase_TO_tbl_letter_of_purchase_approver_1 FOREIGN KEY (letter_of_purchase_code) REFERENCES tbl_letter_of_purchase (letter_of_purchase_code),
  CONSTRAINT FK_tbl_member_TO_tbl_letter_of_purchase_approver_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_order_print;
CREATE TABLE tbl_order_print (
  order_print_code int(11) NOT NULL AUTO_INCREMENT,
  reason varchar(500) NOT NULL,
  printed_at datetime NOT NULL COMMENT '생성일시와 같은 말',
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  letter_of_purchase_code int(11) NOT NULL COMMENT '구매품의서는 발주서의 모든 정보를 포함함',
  PRIMARY KEY (order_print_code),
  UNIQUE KEY order_print_code (order_print_code),
  KEY FK_tbl_letter_of_purchase_TO_tbl_order_print_1 (letter_of_purchase_code),
  KEY FK_tbl_member_TO_tbl_order_print_1 (member_code),
  CONSTRAINT FK_tbl_letter_of_purchase_TO_tbl_order_print_1 FOREIGN KEY (letter_of_purchase_code) REFERENCES tbl_letter_of_purchase (letter_of_purchase_code),
  CONSTRAINT FK_tbl_member_TO_tbl_order_print_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_purchase_status_history;
CREATE TABLE tbl_purchase_status_history (
  purchase_status_history_code int(11) NOT NULL AUTO_INCREMENT,
  status enum('REQUESTED','CANCELED','APPROVED','REJECTED') NOT NULL COMMENT '결재요청, 결재요청취소, 발주승인,  발주반려',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  letter_of_purchase_code int(11) NOT NULL COMMENT '구매품의서는 발주서의 모든 정보를 포함함',
  PRIMARY KEY (purchase_status_history_code),
  UNIQUE KEY purchase_status_history_code (purchase_status_history_code),
  KEY FK_tbl_letter_of_purchase_TO_tbl_purchase_status_history_1 (letter_of_purchase_code),
  CONSTRAINT FK_tbl_letter_of_purchase_TO_tbl_purchase_status_history_1 FOREIGN KEY (letter_of_purchase_code) REFERENCES tbl_letter_of_purchase (letter_of_purchase_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_super_category;
CREATE TABLE tbl_super_category (
  super_category_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (super_category_code),
  UNIQUE KEY super_category_code (super_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_sub_category;
CREATE TABLE tbl_sub_category (
  sub_category_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  super_category_code int(11) NOT NULL,
  PRIMARY KEY (sub_category_code),
  UNIQUE KEY sub_category_code (sub_category_code),
  KEY FK_tbl_super_category_TO_tbl_sub_category_1 (super_category_code),
  CONSTRAINT FK_tbl_super_category_TO_tbl_sub_category_1 FOREIGN KEY (super_category_code) REFERENCES tbl_super_category (super_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_item;
CREATE TABLE tbl_item (
  item_code int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  purchase_price int(11) NOT NULL,
  selling_price int(11) NOT NULL,
  image_url varchar(500) DEFAULT NULL,
  safety_stock int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  item_unique_code varchar(255) NOT NULL,
  category_code int(11) NOT NULL,
  PRIMARY KEY (item_code),
  UNIQUE KEY item_code (item_code),
  KEY FK_tbl_sub_category_TO_tbl_item_1 (category_code),
  CONSTRAINT FK_tbl_sub_category_TO_tbl_item_1 FOREIGN KEY (category_code) REFERENCES tbl_sub_category (sub_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_correspondent_item;
CREATE TABLE tbl_correspondent_item (
  correspondent_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (correspondent_code,item_code),
  KEY FK_tbl_item_TO_tbl_correspondent_item_1 (item_code),
  CONSTRAINT FK_tbl_correspondent_TO_tbl_correspondent_item_1 FOREIGN KEY (correspondent_code) REFERENCES tbl_correspondent (correspondent_code),
  CONSTRAINT FK_tbl_item_TO_tbl_correspondent_item_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_item;
CREATE TABLE tbl_exchange_item (
  exchange_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  quantity int(11) NOT NULL,
  PRIMARY KEY (exchange_code,item_code),
  KEY FK_tbl_item_TO_tbl_exchange_item_1 (item_code),
  CONSTRAINT FK_tbl_exchange_TO_tbl_exchange_item_1 FOREIGN KEY (exchange_code) REFERENCES tbl_exchange (exchange_code),
  CONSTRAINT FK_tbl_item_TO_tbl_exchange_item_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_exchange_item_status;
CREATE TABLE tbl_exchange_item_status (
  exchange_stock_history_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  quantity int(11) NOT NULL,
  restock_quantity int(11) NOT NULL,
  PRIMARY KEY (exchange_stock_history_code,item_code),
  KEY FK_tbl_item_TO_tbl_exchange_item_status_1 (item_code),
  CONSTRAINT FK_tbl_exchange_stock_history_TO_tbl_exchange_item_status_1 FOREIGN KEY (exchange_stock_history_code) REFERENCES tbl_exchange_stock_history (exchange_stock_history_code),
  CONSTRAINT FK_tbl_item_TO_tbl_exchange_item_status_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_letter_of_purchase_item;
CREATE TABLE tbl_letter_of_purchase_item (
  item_code int(11) NOT NULL,
  letter_of_purchase_code int(11) NOT NULL COMMENT '구매품의서는 발주서의 모든 정보를 포함함',
  quantity int(11) NOT NULL,
  storage_confirmed tinyint(1) NOT NULL,
  PRIMARY KEY (item_code,letter_of_purchase_code),
  KEY FK_tbl_letter_of_purchase_TO_tbl_letter_of_purchase_item_1 (letter_of_purchase_code),
  CONSTRAINT FK_tbl_item_TO_tbl_letter_of_purchase_item_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_letter_of_purchase_TO_tbl_letter_of_purchase_item_1 FOREIGN KEY (letter_of_purchase_code) REFERENCES tbl_letter_of_purchase (letter_of_purchase_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_mandatory_purchase;
CREATE TABLE tbl_mandatory_purchase (
  mandatory_purchase_code int(11) NOT NULL AUTO_INCREMENT,
  min_quantity int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  item_code int(11) NOT NULL,
  member_code int(11) NOT NULL,
  due_date datetime NOT NULL COMMENT '해당 날짜까지 주문해야 함',
  PRIMARY KEY (mandatory_purchase_code),
  UNIQUE KEY mandatory_purchase_code (mandatory_purchase_code),
  KEY FK_tbl_item_TO_tbl_mandatory_purchase_1 (item_code),
  KEY FK_tbl_member_TO_tbl_mandatory_purchase_1 (member_code),
  CONSTRAINT FK_tbl_item_TO_tbl_mandatory_purchase_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_member_TO_tbl_mandatory_purchase_1 FOREIGN KEY (member_code) REFERENCES tbl_member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_franchise_mandatory_purchase;
CREATE TABLE tbl_franchise_mandatory_purchase (
  franchise_mandatory_purchase_code int(11) NOT NULL AUTO_INCREMENT,
  quantity int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  franchise_code int(11) NOT NULL,
  mandatory_purchase_code int(11) NOT NULL,
  PRIMARY KEY (franchise_mandatory_purchase_code),
  UNIQUE KEY franchise_mandatory_purchase_code (franchise_mandatory_purchase_code),
  KEY FK_tbl_franchise_TO_tbl_franchise_mandatory_purchase_1 (franchise_code),
  KEY FK_tbl_mandatory_purchase_TO_tbl_franchise_mandatory_purchase_1 (mandatory_purchase_code),
  CONSTRAINT FK_tbl_franchise_TO_tbl_franchise_mandatory_purchase_1 FOREIGN KEY (franchise_code) REFERENCES tbl_franchise (franchise_code),
  CONSTRAINT FK_tbl_mandatory_purchase_TO_tbl_franchise_mandatory_purchase_1 FOREIGN KEY (mandatory_purchase_code) REFERENCES tbl_mandatory_purchase (mandatory_purchase_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_order_item;
CREATE TABLE tbl_order_item (
  order_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  quantity int(11) NOT NULL,
  available enum('AVAILABLE','UNAVAILABLE') NOT NULL,
  part_sum_price int(11) NOT NULL COMMENT '주문 당시 품목에 대한 결제 금액(단일 품목 가격 * 주문수량)',
  PRIMARY KEY (order_code,item_code),
  KEY FK_tbl_item_TO_tbl_order_item_1 (item_code),
  CONSTRAINT FK_tbl_item_TO_tbl_order_item_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_order_TO_tbl_order_item_1 FOREIGN KEY (order_code) REFERENCES tbl_order (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_refund_item_status;
CREATE TABLE tbl_refund_item_status (
  return_refund_history_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  completed tinyint(1) NOT NULL,
  PRIMARY KEY (return_refund_history_code,item_code),
  KEY FK_tbl_item_TO_tbl_refund_item_status_1 (item_code),
  CONSTRAINT FK_tbl_item_TO_tbl_refund_item_status_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_return_refund_history_TO_tbl_refund_item_status_1 FOREIGN KEY (return_refund_history_code) REFERENCES tbl_return_refund_history (return_refund_history_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_item;
CREATE TABLE tbl_return_item (
  item_code int(11) NOT NULL,
  return_code int(11) NOT NULL,
  quantity int(11) NOT NULL,
  PRIMARY KEY (item_code,return_code),
  KEY FK_tbl_return_TO_tbl_return_item_1 (return_code),
  CONSTRAINT FK_tbl_item_TO_tbl_return_item_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_return_TO_tbl_return_item_1 FOREIGN KEY (return_code) REFERENCES tbl_return (return_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_return_item_status;
CREATE TABLE tbl_return_item_status (
  item_code int(11) NOT NULL,
  return_stock_history_code int(11) NOT NULL,
  quantity int(11) NOT NULL,
  restock_quantity int(11) NOT NULL,
  PRIMARY KEY (item_code,return_stock_history_code),
  KEY FK_tbl_return_stock_history_TO_tbl_return_item_status_1 (return_stock_history_code),
  CONSTRAINT FK_tbl_item_TO_tbl_return_item_status_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_return_stock_history_TO_tbl_return_item_status_1 FOREIGN KEY (return_stock_history_code) REFERENCES tbl_return_stock_history (return_stock_history_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;

DROP TABLE IF EXISTS tbl_stock;
CREATE TABLE tbl_stock (
  storage_code int(11) NOT NULL,
  item_code int(11) NOT NULL,
  available_stock int(11) NOT NULL,
  out_stock int(11) NOT NULL,
  in_stock int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (storage_code,item_code),
  KEY FK_tbl_item_TO_tbl_stock_1 (item_code),
  CONSTRAINT FK_tbl_item_TO_tbl_stock_1 FOREIGN KEY (item_code) REFERENCES tbl_item (item_code),
  CONSTRAINT FK_tbl_storage_TO_tbl_stock_1 FOREIGN KEY (storage_code) REFERENCES tbl_storage (storage_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
