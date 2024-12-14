package com.varc.brewnetapp.domain.purchase.command.domain.repository;

import com.varc.brewnetapp.domain.purchase.command.domain.aggregate.PurchasePosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasePositionRepository extends JpaRepository<PurchasePosition, Integer> {
}
