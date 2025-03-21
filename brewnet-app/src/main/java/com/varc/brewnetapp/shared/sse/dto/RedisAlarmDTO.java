package com.varc.brewnetapp.shared.sse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RedisAlarmDTO {

    private String message;
    private String eventName;
    private Integer senderMemberCode;
}
