package com.varc.brewnetapp.domain.delivery.query.controller;

import com.varc.brewnetapp.common.ResponseMessage;
import com.varc.brewnetapp.domain.delivery.command.domain.aggregate.DeliveryKind;
import com.varc.brewnetapp.domain.delivery.query.dto.DeliveryDTO;
import com.varc.brewnetapp.domain.delivery.query.dto.DeliveryDetailDTO;
import com.varc.brewnetapp.domain.delivery.query.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "queryDeliveryController")
@RequestMapping("api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("")
    @Operation(summary = "주문 배송 목록 조회. 배송 기사만 가능 / " 
        + "deliveryKind는 ORDER나 EXCHANGE, RETURN 값만 가능합니다 "
        + "/ RETURN과 EXCHANGE는 각각의 배송 목록 결과가 합쳐져 response 됩니다" 
        + "/ Response로 드리는 값 중 deliveryStatus는 화면에 보이는 결재 코드입니다. 한국어로 변환해서 바로 드립니다")
    public ResponseEntity<ResponseMessage<Page<DeliveryDTO>>> findDeliveryList(@PageableDefault(page = 0, size = 10) Pageable page,
        @RequestParam DeliveryKind deliveryKind) {

        return ResponseEntity.ok(new ResponseMessage<>(
            200, "배송 목록 조회 성공", deliveryService.findDeliveryList(deliveryKind, page)));
    }

    @GetMapping("/detail")
    @Operation(summary = "주문 배송 조회. 배송 기사만 가능 / token을 주면 현재 진행중인 배송 1개를 반환 ")
    public ResponseEntity<ResponseMessage<DeliveryDetailDTO>> findDeliveryDetail(@RequestHeader("Authorization") String accessToken) {

        return ResponseEntity.ok(new ResponseMessage<>(
            200, "주문 배송 조회 성공", deliveryService.findDeliveryDetail(accessToken)));
    }


}
