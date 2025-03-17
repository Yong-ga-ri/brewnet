package com.varc.brewnetapp.domain.order.command.domain.repository;

import com.varc.brewnetapp.domain.order.command.domain.aggregate.entity.OrderApprover;
import com.varc.brewnetapp.domain.order.command.domain.aggregate.entity.compositionkey.OrderApprovalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderApprovalRepository extends JpaRepository<OrderApprover, OrderApprovalCode> {
    List<OrderApprover> findByOrderApprovalCode_OrderCode(int orderApprovalCode);

    @Query("SELECT " +
                  "CASE WHEN COUNT(oa) > 0 " +
                       "THEN true " +
                       "ELSE false " +
                  "END " +
             "FROM OrderApprover oa " +
            "WHERE oa.active = true " +
              "AND oa.orderApprovalCode.orderCode = :orderCode")
    boolean checkIsOrderDrafted(@Param("orderCode") int orderCode);
}
