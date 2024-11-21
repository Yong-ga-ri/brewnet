package com.varc.brewnetapp.domain.exchange.command.application.service;

import com.varc.brewnetapp.common.domain.drafter.DrafterApproved;
import com.varc.brewnetapp.common.domain.exchange.Availability;
import com.varc.brewnetapp.domain.exchange.command.application.repository.*;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.entity.*;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.ex_entity.ExOrder;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.ex_entity.ExOrderItem;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.ex_entity.ExOrderItemCode;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.vo.ExchangeApproveReqVO;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.vo.ExchangeReqItemVO;
import com.varc.brewnetapp.domain.exchange.command.domain.aggregate.vo.ExchangeReqVO;
import com.varc.brewnetapp.common.domain.approve.Approval;
import com.varc.brewnetapp.common.domain.exchange.ExchangeStatus;
import com.varc.brewnetapp.domain.member.command.domain.aggregate.entity.Member;
import com.varc.brewnetapp.domain.member.command.domain.repository.MemberRepository;
import com.varc.brewnetapp.exception.ExchangeNotFoundException;
import com.varc.brewnetapp.exception.InvalidStatusException;
import com.varc.brewnetapp.exception.MemberNotFoundException;
import com.varc.brewnetapp.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("ExchangeServiceCommand")
@RequiredArgsConstructor
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {

    // 이후 의존성 수정 필요
    private final ExchangeRepository exchangeRepository;
    private final ExOrderRepository exOrderRepository;  // 임시
    private final ExOrderItemRepository exOrderItemRepository; // 임시
    private final MemberRepository memberRepository;
    private final ExchangeItemRepository exchangeItemRepository;
    private final ExchangeStatusHistoryRepository exchangeStatusHistoryRepository;
    private final com.varc.brewnetapp.domain.exchange.query.service.ExchangeServiceImpl exchangeServiceQuery;
    private final ExchangeApproverRepository exchangeApproverRepository;

    @Override
    @Transactional
    public void createExchange(String loginId, ExchangeReqVO exchangeReqVO) {
        /*
         * 교환 신청 시 변하는 상태값
         * [1] 교환 결재 상태           - tbl_exchange : approvalStatus = UNCONFIRMED
         * [2] 기안자의 교환 승인 여부    - tbl_exchange : drafterApproved = NONE
         * [3] 교환상태                - tbl_exchange_status_history : status = REQUESTED
         * [4] 반품/교환요청 가능여부     - tbl_order_item : available = UNAVAILABLE
        * */

        // 1. 해당 주문이 이 가맹점의 주문이 맞는지 확인
        ExOrder order = exOrderRepository.findById(exchangeReqVO.getOrderCode()).orElse(null);

        if (exchangeServiceQuery.isValidOrderByFranchise(loginId, exchangeReqVO.getOrderCode())) {

            // 2. 교환 객체 생성
            Exchange exchange = Exchange.builder()
                    .comment(null)
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .active(true)
                    .reason(exchangeReqVO.getReason())
                    .explanation(exchangeReqVO.getExplanation())
                    .approvalStatus(Approval.UNCONFIRMED)   // [1] 교환 결재 상태
                    .order(order)
                    .memberCode(null)
                    .delivery(null)
                    .drafterApproved(DrafterApproved.NONE)  // [2] 기안자의 교환 승인 여부
                    .sumPrice(exchangeReqVO.getSumPrice())
                    .build();

            exchangeRepository.save(exchange);


            for (ExchangeReqItemVO reqItem : exchangeReqVO.getExchangeItemList()) {
                // 3. 주문 별 상품 - 반품/교환요청 가능여부 변경
                // 복합키 객체 생성
                ExOrderItemCode exOrderItemCode = ExOrderItemCode.builder()
                        .orderCode(order.getOrderCode())            // 주문 코드 설정
                        .itemCode(reqItem.getItemCode())            // 상품 코드 설정
                        .build();

                ExOrderItem exOrderItem = exOrderItemRepository.findByOrderItemCode(exOrderItemCode)
                        .orElseThrow(() -> new ExchangeNotFoundException("존재하지 않는 주문 상품이 포함되어 있습니다."));
                if (exOrderItem.getAvailable() != Availability.AVAILABLE) {
                    throw new IllegalArgumentException("교환 신청이 불가한 주문 상품이 포함되어 있습니다.");
                }

                exOrderItem = exOrderItem.toBuilder()
                        .available(Availability.UNAVAILABLE)        // [4] 반품/교환요청 가능여부
                        .build();

                exOrderItemRepository.save(exOrderItem);

                // 4. 교환별상품 저장
                // 복합키 객체 생성
                ExchangeItemCode exchangeItemCode = ExchangeItemCode.builder()
                        .exchangeCode(exchange.getExchangeCode())   // 교환 코드 설정
                        .itemCode(reqItem.getItemCode())            // 상품 코드 설정
                        .build();

                // ExchangeItem 객체 생성
                ExchangeItem exchangeItem = ExchangeItem.builder()
                        .exchangeItemCode(exchangeItemCode)         // 복합키 설정
                        .quantity(exOrderItem.getQuantity())        // 기존 주문 별 상품의 수량 그대로 저장
                        .build();

                exchangeItemRepository.save(exchangeItem);

            }

            // 5. 교환상태이력 저장
            saveExchangeStatusHistory(ExchangeStatus.REQUESTED, exchange);

            // 6. 교환품목사진 저장

        } else {
            throw new UnauthorizedAccessException("로그인한 가맹점에서 작성한 주문에 대해서만 교환 요청할 수 있습니다");
        }
    }

    @Override
    @Transactional
    public boolean cancelExchange(String loginId, Integer exchangeCode) {
        /*
         * 교환 취소 시 변하는 상태값
         * [1] 교환상태                - tbl_exchange_status_history : status = CANCELED (내역 추가됨)
         * [2] 활성화                  - tbl_exchange : active = False
         * [3] 반품/교환요청 가능여부     - tbl_order_item : available = AVAILABLE
        * */

        /*
         * 교환 취소 가능한 조건
         * 1. 교환 상태 이력(tbl_exchange_status_history)테이블의 교환상태(status)가 REQUESTED인 경우
        * */

        // 1. 해당 취소요청의 교환내역이 이 가맹점에서 작성한 것이 맞는지 확인
        if (exchangeServiceQuery.isValidExchangeByFranchise(loginId, exchangeCode)) {

            Exchange exchange = exchangeRepository.findById(exchangeCode)
                    .orElseThrow(() -> new ExchangeNotFoundException("교환 코드가 존재하지 않습니다."));

            ExchangeStatus exchangeStatus = exchangeServiceQuery.findExchangeLatestStatus(exchangeCode);

            // status가 REQUESTED인 경우에만 취소 가능
            if (exchangeStatus == ExchangeStatus.REQUESTED) {


                // 2. 교환 상태 이력(tbl_exchange_status_history)테이블에 취소내역 저장
                saveExchangeStatusHistory(ExchangeStatus.CANCELED, exchange);

                // 3. 교환(tbl_exchange) 테이블의 활성화(active)를 false로 변경
                exchange = exchange.toBuilder()     // 새 객체가 생성되지만, 영속성 컨텍스트는 아님
                        .active(false)              // [2] 활성화
                        .build();
                exchangeRepository.save(exchange);  //객체가 영속성 컨텍스트에 저장되고, 엔티티 매니저가 이 객체를 관리함


                // 4. 교환 별 상품 -> 주문 별 상품의 반품/교환요청 가능여부(available)을 AVAILABLE로 변경
                // 주문 별 상품(tbl_order_item)의
                // 교환 별 상품 리스트 -> 주문 별 상품 하나씩 조회 -> 상태값 변경
                List<ExchangeItem> exchangeItemList = exchangeItemRepository.findByExchangeItemCode_ExchangeCode(exchange.getExchangeCode());

                for (ExchangeItem exchangeItem : exchangeItemList) {

                    ExOrderItemCode orderItemCode = ExOrderItemCode.builder()
                            .orderCode(exchange.getOrder().getOrderCode())              // 주문 코드 설정
                            .itemCode(exchangeItem.getExchangeItemCode().getItemCode()) // 상품 코드 설정
                            .build();

                    ExOrderItem orderItem = exOrderItemRepository.findByOrderItemCode(orderItemCode)
                            .orElseThrow(() -> new ExchangeNotFoundException("교환할 상품이 존재하지 않습니다."));

                    orderItem = orderItem.toBuilder()
                            .available(Availability.AVAILABLE) // [3] 반품/교환요청 가능여부
                            .build();
                    exOrderItemRepository.save(orderItem);
                }

                return true;
            } else {
                throw new InvalidStatusException("교환신청 취소가 불가능합니다. 교환상태가 '교환요청'인 경우에만 취소할 수 있습니다");
            }
        } else {
            throw new UnauthorizedAccessException("로그인한 가맹점에서 작성한 교환요청에 대해서만 취소할 수 있습니다");
        }
    }

    @Override
    @Transactional
    public void approveExchange(String loginId, ExchangeApproveReqVO exchangeApproveReqVO) {
        /*
         * 교환결재신청(최초기안자) 시 변하는 상태값
         * [1] 기안자의 교환 승인 여부    - tbl_exchange : drafter_approved = REJECT / APPROVE
         * [2] 교환 결재 상태            - tbl_exchange : approval_status = CANCELED / UNCONFIRMED
         * [3] 교환상태                 - tbl_exchange_status_history : status = REJECTED / PENDING (내역 추가됨)
         * [4] 승인여부                 - tbl_exchange_approver : approved = REJECTED / APPROVED & UNCONFIRMED (결재자 등록됨)
        * */

        /*
         * 교환 결재 신청(최초기안자)
         * - 결재 신청이 가능한 조건:
         *   1. 교환상태이력(tbl_exchange_status_history) 테이블 교환상태(status) != REQUESTED
         *   2. 교환 결재 상태(approved) == UNCONFIRMED
         *   3. 기안자의 교환 승인 여부(drafter_approved) == NONE
         *   4. 교환 기안자(member_code) == null
        * */

        // 1. 교한 결재 신청이 가능한지 확인
        Exchange exchange = exchangeRepository.findById(exchangeApproveReqVO.getExchangeCode())
                .orElseThrow(() -> new ExchangeNotFoundException("교환 코드가 존재하지 않습니다."));

        ExchangeStatus status = exchangeServiceQuery.findExchangeLatestStatus(exchange.getExchangeCode());
        if (status != ExchangeStatus.REQUESTED) {
            throw new InvalidStatusException("결재신청이 불가능합니다. 교환 상태가 '교환요청'이 아닙니다.");
        } else if (exchange.getApprovalStatus() != Approval.UNCONFIRMED) {
            throw new InvalidStatusException("결재신청이 불가능합니다. 교환 결재 상태가 '미확인'이 아닙니다.");
        } else if (exchange.getDrafterApproved() != DrafterApproved.NONE) {
            throw new InvalidStatusException("결재신청이 불가능합니다. 이미 다른 관리자가 결재 등록했습니다.");
        } else if (exchange.getMemberCode() != null) {
            throw new InvalidStatusException("결재신청이 불가능합니다. 이미 다른 관리자가 진행 중인 교환 내역입니다.");
        }

        // 2. 교환(tbl_exchange) 테이블 '기안자의 교환 승인 여부(drafter_approved)' 변경
        Member member = memberRepository.findById(loginId)
                .orElseThrow(() -> new MemberNotFoundException("해당 아이디의 회원이 존재하지 않습니다."));

        if (exchangeApproveReqVO.getApproval() == DrafterApproved.REJECT) {
            drafterRejectExchange(exchangeApproveReqVO, exchange, member);
        } else if (exchangeApproveReqVO.getApproval() == DrafterApproved.APPROVE) {
            drafterApproveExchange(exchangeApproveReqVO, exchange, member);
        } else {
            throw new InvalidStatusException("최초 기안자의 결재승인여부 값이 잘못되었습니다. 승인 또는 반려여야 합니다.");
        }
    }

    private void drafterApproveExchange(ExchangeApproveReqVO exchangeApproveReqVO, Exchange exchange, Member member) {
        // [1] 교환(tbl_exchange) 테이블 '기안자의 교환 승인 여부(drafter_approved)'가 APPROVE 라면
        //      -> 교환(tbl_exchange) 테이블 '교환 결재 상태(approval_status)' = UNCONFIRMED (변화 X)
        //      -> 교환 상태 이력(tbl_exchange_status_history) 테이블 '교환 상태(status)' = PENDING (내역 추가됨)
        //      -> 교환 별 결재자들(tbl_exchange_approver) 테이블 '승인여부(approved)' = APPROVED(기안자) / UNCONFIRMED(그 외 결재자)

        exchange = exchange.toBuilder()
                .drafterApproved(DrafterApproved.APPROVE)               // [1] 기안자의 교환 승인 여부
//                    .approvalStatus(Approval.UNCONFIRMED)                 // [2] 교환 결재 상태 (변화 X)
                .memberCode(member)                                     // 기안자 등록
                .comment(exchangeApproveReqVO.getComment())             // 첨언 등록
                .build();
        exchangeRepository.save(exchange);

        // 교환 상태 이력(tbl_exchange_status_history) 테이블 '교환 상태(status)' = PENDING (내역 추가됨)
        saveExchangeStatusHistory(ExchangeStatus.PENDING, exchange);

        // 교환 별 결재자들(tbl_exchange_approver) 테이블 '승인여부(approved)' = APPROVED (기안자만 결재자로 등록됨)
        saveExchangeApprover(member.getMemberCode(), exchange, Approval.APPROVED, String.valueOf(LocalDateTime.now()), exchange.getComment());

        // 3-2. 결재자들 등록
        for (Integer approverCode : exchangeApproveReqVO.getApproverCodeList()) {
            saveExchangeApprover(approverCode, exchange, Approval.UNCONFIRMED, null, null);
        }
    }

    private void drafterRejectExchange(ExchangeApproveReqVO exchangeApproveReqVO, Exchange exchange, Member member) {
        // [1] 교환(tbl_exchange) 테이블 '기안자의 교환 승인 여부(drafter_approved)'가 REJECT 라면
        //      -> [2] 교환(tbl_exchange) 테이블 '교환 결재 상태(approval_status)' = REJECTED
        //      -> [3] 교환 상태 이력(tbl_exchange_status_history) 테이블 '교환 상태(status)' = REJECTED (내역 추가됨)
        //      -> [4] 교환 별 결재자들(tbl_exchange_approver) 테이블 '승인여부(approved)' = REJECTED (기안자만 결재자로 등록됨)

        exchange = exchange.toBuilder()
                .drafterApproved(DrafterApproved.REJECT)                // [1] 기안자의 교환 승인 여부
                .approvalStatus(Approval.REJECTED)                      // [2] 교환 결재 상태
                .memberCode(member)                                     // 기안자 등록
                .comment(exchangeApproveReqVO.getComment())             // 첨언 등록
                .build();
        exchangeRepository.save(exchange);

        // 교환 상태 이력(tbl_exchange_status_history) 테이블 '교환 상태(status)' = REJECTED (내역 추가됨)
        saveExchangeStatusHistory(ExchangeStatus.REJECTED, exchange);

        // 교환 별 결재자들(tbl_exchange_approver) 테이블 '승인여부(approved)' = REJECTED (기안자만 결재자로 등록됨)
        saveExchangeApprover(member.getMemberCode(), exchange, Approval.REJECTED, String.valueOf(LocalDateTime.now()), exchange.getComment());
    }

    private void saveExchangeApprover(Integer member, Exchange exchange, Approval rejected, String createdAt, String exchange1) {
        ExchangeApproverCode drafterApproverCode = ExchangeApproverCode.builder()
                .memberCode(member)         // 멤버 코드 설정
                .exchangeCode(exchange.getExchangeCode())   // 교환 코드 설정
                .build();
        ExchangeApprover drafterApprover = ExchangeApprover.builder()
                .exchangeApproverCode(drafterApproverCode)  // 복합키 설정
                .approved(rejected)                // [4] 교환 별 결재자들 (기안자)
                .createdAt(createdAt)
                .comment(exchange1)
                .active(true)
                .build();
        exchangeApproverRepository.save(drafterApprover);
    }

    private void saveExchangeStatusHistory(ExchangeStatus status, Exchange exchange) {
        ExchangeStatusHistory exchangeStatusHistory = ExchangeStatusHistory.builder()
                .status(status)   // [3] 교환 상태 이력
                .createdAt(String.valueOf(LocalDateTime.now()))
                .active(true)
                .exchange(exchange)
                .build();
        exchangeStatusHistoryRepository.save(exchangeStatusHistory);
    }
}
