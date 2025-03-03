package com.varc.brewnetapp.domain.order.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderValidateMapper {
    boolean checkIsOrderDrafted(
            @Param("orderCode") int orderCode
    );
}
