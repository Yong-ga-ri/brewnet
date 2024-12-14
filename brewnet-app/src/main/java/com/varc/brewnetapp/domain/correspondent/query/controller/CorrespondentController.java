package com.varc.brewnetapp.domain.correspondent.query.controller;

import com.varc.brewnetapp.common.ResponseMessage;
import com.varc.brewnetapp.domain.correspondent.common.PageResponse;
import com.varc.brewnetapp.domain.correspondent.query.dto.CorrespondentDTO;
import com.varc.brewnetapp.domain.correspondent.query.dto.CorrespondentItemDTO;
import com.varc.brewnetapp.domain.correspondent.query.service.CorrespondentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("CorrespondentControllerQuery")
@RequestMapping("api/v1/hq/correspondent")
public class CorrespondentController {

    private final CorrespondentService correspondentService;

    @Autowired
    public CorrespondentController(CorrespondentService correspondentService) {
        this.correspondentService = correspondentService;
    }

    @GetMapping("")
    @Operation(summary = "거래처 목록 조회 API (거래처 코드, 거래처명 중 하나로 검색 가능) - pageNumber의 default값은 1," +
            " pageSize의 default값은 10")
    public ResponseEntity<ResponseMessage<PageResponse<List<CorrespondentDTO>>>> selectAllCorrespondents(
                                            @RequestAttribute("loginId") String loginId,
                                            @RequestParam(required = false) Integer correspondentCode,
                                            @RequestParam(required = false) String correspondentName,
                                            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PageResponse<List<CorrespondentDTO>> response = correspondentService
                                                        .selectAllCorrespondents(loginId,
                                                                                correspondentCode,
                                                                                correspondentName,
                                                                                pageNumber,
                                                                                pageSize);

        return ResponseEntity.ok(new ResponseMessage<>(200, "거래처 목록 조회 성공", response));
    }

    @GetMapping("/items")
    @Operation(summary = "거래처 클릭 시 해당 거래처의 취급 상품 목록 조회 API (거래처코드는 필수 " +
            "/ 상품고유코드, 상품명 중 하나로 검색 가능) - pageNumber의 default값은 1, pageSize의 default값은 10")
    public ResponseEntity<ResponseMessage<PageResponse<List<CorrespondentItemDTO>>>> selectItemsOfCorrespondent(
            @RequestAttribute("loginId") String loginId,
            @RequestParam Integer correspondentCode,
            @RequestParam(required = false) String itemUniqueCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PageResponse<List<CorrespondentItemDTO>> response = correspondentService
                                                            .selectItemsOfCorrespondent(loginId,
                                                                                        correspondentCode,
                                                                                        itemUniqueCode,
                                                                                        itemName,
                                                                                        pageNumber,
                                                                                        pageSize);

        return ResponseEntity.ok(new ResponseMessage<>(200, "거래처의 취급 상품 목록 조회 성공", response));
    }

    @GetMapping("/print")
    @Operation(summary = "거래처 목록을 엑셀 파일로 출력할 때 사용하는 API")
    public ResponseEntity<ResponseMessage<List<CorrespondentDTO>>> printAllCorrespondents(
                                                    @RequestParam(required = false) Integer correspondentCode,
                                                    @RequestParam(required = false) String correspondentName) {

        List<CorrespondentDTO> correspondentList = correspondentService
                                                    .printAllCorrespondents(correspondentCode, correspondentName);

        return ResponseEntity.ok(new ResponseMessage<>(200, "거래처 목록 파일 출력 성공", correspondentList));
    }

    @GetMapping("/print-items")
    @Operation(summary = "거래처의 활성화된 상품 목록을 엑셀 파일로 출력할 때 사용하는 API (거래처코드는 필수)")
    public ResponseEntity<ResponseMessage<List<CorrespondentItemDTO>>> printCorrespondentActiveItems(
                                                    @RequestParam Integer correspondentCode,
                                                    @RequestParam(required = false) String itemUniqueCode,
                                                    @RequestParam(required = false) String itemName) {

        List<CorrespondentItemDTO> itemList = correspondentService
                                                .printCorrespondentActiveItems(correspondentCode,
                                                                                itemUniqueCode,
                                                                                itemName);

        return ResponseEntity.ok(new ResponseMessage<>(
                                    200, "거래처의 발주 가능 상품 목록 파일 출력 성공", itemList));
    }

    @GetMapping("/{correspondentCode}")
    @Operation(summary = "거래처 코드로 거래처 상세 정보 불러오는 API")
    public ResponseEntity<ResponseMessage<CorrespondentDTO>> getCorrespondentDetail(
                                                                @PathVariable int correspondentCode) {

        CorrespondentDTO correspondentInfo = correspondentService.getCorrespondentDetail(correspondentCode);

        return ResponseEntity.ok(new ResponseMessage<>(200, "거래처 상세 정보 조회 성공", correspondentInfo));
    }
}
