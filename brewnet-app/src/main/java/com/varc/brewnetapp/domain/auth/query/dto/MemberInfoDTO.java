package com.varc.brewnetapp.domain.auth.query.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberInfoDTO {
    private String refreshToken;
    private int memberCode;
    private Integer franchiseCode;
}
