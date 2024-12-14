package com.varc.brewnetapp.domain.purchase.command.domain.repository;

import com.varc.brewnetapp.domain.purchase.command.domain.aggregate.PurchaseSeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseSealRepository extends JpaRepository<PurchaseSeal, Integer> {

    PurchaseSeal findTopByActiveTrueOrderBySealCodeDesc();
}
