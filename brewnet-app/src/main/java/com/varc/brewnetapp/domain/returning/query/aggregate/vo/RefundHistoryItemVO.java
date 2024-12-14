package com.varc.brewnetapp.domain.returning.query.aggregate.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefundHistoryItemVO {
    private String itemUniqueCode;  // 상품 고유 코드
    private String itemName;        // 상품명
    private String superCategory;   // 상위 카테고리
    private String subCategory;     // 하위 카테고리
    private int quantity;           // 총수량
    private int refundPrice;        // 환불금액
    private boolean completed;      // 환불완료여부
}
