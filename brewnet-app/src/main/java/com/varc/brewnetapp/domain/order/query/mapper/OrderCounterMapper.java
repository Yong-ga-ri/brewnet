package com.varc.brewnetapp.domain.order.query.mapper;

import com.varc.brewnetapp.shared.request.Retrieve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderCounterMapper {

    // for HQ
    int countOrdersForHq(@Param("retrieve") Retrieve retrieve);
    int countSearchedOrdersForHQByOrderCode(@Param("retrieve") Retrieve retrieve);
    int countSearchedOrdersForHQByOrderedFranchiseName(@Param("retrieve") Retrieve retrieve);
    int countSearchedOrdersForHQByOrderManager(@Param("retrieve") Retrieve retrieve);

    // for FRANCHISE
    int countOrdersForFranchise(
            @Param("retrieve") Retrieve retrieve,
            @Param("franchiseCode") int franchiseCode
    );
    int countSearchedOrdersForFranchiseByOrderCode(
            @Param("retrieve") Retrieve retrieve,
            @Param("franchiseCode") int franchiseCode
    );
    int countSearchedOrdersForFranchiseByItemName(
            @Param("retrieve") Retrieve retrieve,
            @Param("franchiseCode") int franchiseCode
    );
}
