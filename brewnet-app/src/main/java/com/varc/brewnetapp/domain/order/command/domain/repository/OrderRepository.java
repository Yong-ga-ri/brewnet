package com.varc.brewnetapp.domain.order.command.domain.repository;

import com.varc.brewnetapp.domain.order.command.domain.aggregate.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT " +
                  "CASE WHEN COUNT(o) > 0 " +
                       "THEN true " +
                       "ELSE false " +
                  "END " +
             "FROM Order o " +
            "WHERE o.active = true " +
              "AND o.orderCode = :orderCode " +
              "AND o.franchiseCode = :franchiseCode ")
    boolean checkIsOrderFrom(
            @Param("franchiseCode") int franchiseCode,
            @Param("orderCode") int orderCode
    );

}
