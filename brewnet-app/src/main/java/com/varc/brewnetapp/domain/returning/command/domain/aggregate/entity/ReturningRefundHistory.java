package com.varc.brewnetapp.domain.returning.command.domain.aggregate.entity;

import com.varc.brewnetapp.shared.domain.approve.Confirmed;
import com.varc.brewnetapp.shared.domain.returning.ReturningRefundStatus;
import jakarta.persistence.*;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "tbl_return_refund_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ReturningRefundHistory {
    @Id
    @Column(name = "return_refund_history_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int returnRefundHistoryCode;        // 환불완료 내역 코드

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturningRefundStatus status;       // 처리상태

    @Column(name = "manager", nullable = false)
    private String manager;                     // 처리담당자

    @Column(name = "comment", nullable = true)
    private String comment;                     // 처리 중 비고사항

    @Column(name = "confirmed", nullable = false)
    @Enumerated(EnumType.STRING)
    private Confirmed confirmed;                // 내역 확인 여부

    @Column(name = "created_at", nullable = false)
    private String created_at;                  // 생성일시

    @Column(name = "active", nullable = false)
    private boolean active;                     // 활성화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_code", nullable = false)
    private Returning returning;
}