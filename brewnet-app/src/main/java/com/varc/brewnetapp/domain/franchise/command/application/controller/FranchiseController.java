package com.varc.brewnetapp.domain.franchise.command.application.controller;

import com.varc.brewnetapp.common.ResponseMessage;
import com.varc.brewnetapp.domain.franchise.command.application.dto.CreateFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.application.dto.DeleteFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.application.dto.UpdateFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.application.service.FranchiseService;
import com.varc.brewnetapp.domain.member.command.application.dto.CreateCompanyRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "commandFranchiseController")
@RequestMapping("api/v1/responsible/franchise")
public class FranchiseController {

    private FranchiseService franchiseService;

    @Autowired
    public FranchiseController(FranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    // 마스터만 가능
    @PostMapping("")
    @Operation(summary = "(api path 변경됨) 가맹점 정보 생성 API. 책임자급 이상만 가능")
    public ResponseEntity<ResponseMessage<Object>> createFranchise(@RequestBody
        CreateFranchiseRequestDTO createFranchiseRequestDTO) {

        franchiseService.createFranchise(createFranchiseRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "가맹점 정보 생성 성공", null));
    }

    // 마스터만 가능
    @PutMapping("")
    @Operation(summary = "(api path 변경됨) 가맹점 정보 수정 API. 책임자급 이상만 가능")
    public ResponseEntity<ResponseMessage<Object>> updateFranchise(@RequestBody
    UpdateFranchiseRequestDTO updateFranchiseRequestDTO) {

        franchiseService.updateFranchise(updateFranchiseRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "가맹점 정보 수정 성공", null));
    }

    // 마스터만 가능
    @DeleteMapping("")
    @Operation(summary = "(api path 변경됨) 가맹점 정보 삭제 API. 책임자급 이상만 가능")
    public ResponseEntity<ResponseMessage<Object>> deleteFranchise(@RequestBody
    DeleteFranchiseRequestDTO deleteFranchiseRequestDTO) {

        franchiseService.deleteFranchise(deleteFranchiseRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "가맹점 정보 삭제 성공", null));
    }
}
