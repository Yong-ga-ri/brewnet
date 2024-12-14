package com.varc.brewnetapp.domain.purchase.command.domain.aggregate;

import com.varc.brewnetapp.domain.purchase.common.KindOfApproval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_approval")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PurchaseApproval {

    @Id
    @Column(name = "approval_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approvalCode;

    @Column(name = "kind")
    @Enumerated(EnumType.STRING)
    private KindOfApproval kind;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "active")
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_code", nullable = false, insertable = false, updatable = false)
    private PurchasePosition purchasePosition;
}
