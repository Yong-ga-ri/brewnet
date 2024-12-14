package com.varc.brewnetapp.domain.statistics.query.dto;

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
public class OrderItemStatisticsDTO {
    private String itemName;
    private double itemPercent;
    private int itemCount;
}
