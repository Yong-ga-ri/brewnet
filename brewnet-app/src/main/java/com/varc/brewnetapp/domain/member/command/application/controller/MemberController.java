package com.varc.brewnetapp.domain.member.command.application.controller;

import com.varc.brewnetapp.common.ResponseMessage;
import com.varc.brewnetapp.domain.member.command.application.dto.ChangeMemberRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.ChangePwRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.CheckNumDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.CheckPwRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.ConfirmEmailRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.CreateCompanyRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.LoginIdRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.dto.SendEmailRequestDTO;
import com.varc.brewnetapp.domain.member.command.application.service.CompanyService;
import com.varc.brewnetapp.domain.member.command.application.service.EmailService;
import com.varc.brewnetapp.domain.member.command.application.service.MemberService;
import com.varc.brewnetapp.exception.InvalidEmailCodeException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("api/v1")
@RestController(value = "commandMemberController")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final CompanyService companyService;

    @Autowired
    public MemberController(MemberService memberService, EmailService emailService, CompanyService companyService) {
        this.memberService = memberService;
        this.emailService = emailService;
        this.companyService = companyService;
    }

    // 아무나
    @PostMapping("email/send-email")
    @Operation(summary = "인증 이메일 발송 API / 권한 없는 사람 + 권한 있는 사람 모두 사용 가능")
    public ResponseEntity<ResponseMessage<Object>> sendEmail(@RequestBody SendEmailRequestDTO sendEmailRequestDTO)
        throws MessagingException, UnsupportedEncodingException {
        try {
            emailService.sendSimpleMessage(sendEmailRequestDTO);
            return ResponseEntity.ok(new ResponseMessage<>(200, "인증 이메일 발송에 성공했습니다", null));
        } catch (MailException e) {
            throw new MailSendException("인증 이메일 발송에 실패했습니다.");
        }
    }

    // 아무나
    @PostMapping("email/confirm-email")
    @Operation(summary = "인증 이메일 검증 API / 권한 없는 사람 + 권한 있는 사람 모두 사용 가능")
    public ResponseEntity<ResponseMessage<Object>> confirmEmail(@RequestBody ConfirmEmailRequestDTO confirmEmailRequestDTO) {
        if (emailService.findEmailCode(confirmEmailRequestDTO))
            return ResponseEntity.ok(new ResponseMessage<>(200, "이메일 인증에 성공했습니다", null));
        else
            throw new InvalidEmailCodeException("이메일 인증에 실패했습니다");
    }

    // 아무나
    @PutMapping("/auth/pw")
    @Operation(summary = "(api path 변경됨) 로그인 전인 회원의 비밀번호 변경 API / 권한 없는 사람 + 권한 있는 사람 모두 사용 가능")
    public ResponseEntity<ResponseMessage<Object>> changePassword(@RequestBody ChangePwRequestDTO changePwRequestDTO) {
        if (memberService.changePassword(changePwRequestDTO)) {
            return ResponseEntity.ok(new ResponseMessage<>(200, "비밀번호 변경에 성공했습니다", null));
        } else {
            throw new InvalidEmailCodeException("비밀번호 변경에 실패했습니다");
        }
    }

    @DeleteMapping("/member")
    @Operation(summary = "회원 계정 비활성화 API")
    public ResponseEntity<ResponseMessage<Object>> deleteMember(@RequestHeader("Authorization") String accessToken,
        @RequestBody LoginIdRequestDTO loginIdRequestDTO){

        memberService.deleteMember(accessToken, loginIdRequestDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200, "계정 삭제 완료", null));
    }

    @PutMapping("/member/{memberCode}")
    @Operation(summary = "회원 정보 수정 API")
    public ResponseEntity<ResponseMessage<Object>> changeMember(@RequestHeader("Authorization") String accessToken,
                                                                @RequestBody ChangeMemberRequestDTO changeMemberRequestDTO,
        @PathVariable(value = "memberCode") int memberCode) {

        memberService.changeMember(accessToken, changeMemberRequestDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "회원 정보 수정 성공", null));
    }

    @PostMapping("/master/company")
    @Operation(summary = "(api path 변경됨) 회사 정보 생성 API. master만 가능")
    public ResponseEntity<ResponseMessage<Object>> createCompany(@RequestHeader("Authorization") String accessToken,
        @RequestBody CreateCompanyRequestDTO createCompanyRequestDTO) {

        companyService.createCompany(accessToken, createCompanyRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "회사 정보 생성 성공", null));
    }

    @PutMapping("/master/company")
    @Operation(summary = "(api path 변경됨) 회사 정보 수정 API. master만 가능")
    public ResponseEntity<ResponseMessage<Object>> updateCompany(@RequestHeader("Authorization") String accessToken,
        @RequestBody CreateCompanyRequestDTO createCompanyRequestDTO) {

        companyService.updateCompany(accessToken, createCompanyRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "회사 정보 수정 성공", null));
    }

    @PostMapping("/master/company/seal")
    @Operation(summary = "(api path 변경됨) 법인 인감 등록 API. master만 가능")
    public ResponseEntity<ResponseMessage<Object>> createSeal(@RequestHeader("Authorization") String accessToken,
        @RequestPart(value = "sealImage") MultipartFile sealImage) {

        companyService.createSeal(accessToken, sealImage);
        return ResponseEntity.ok(new ResponseMessage<>(200, "법인 인감 등록 성공", null));
    }

    @PutMapping("/master/company/seal")
    @Operation(summary = "(api path 변경됨) 법인 인감 수정 API. master만 가능 / 법인 인감이 없다면 생성해줌. 즉, POST, DELETE 역할 둘 다 함 / "
        + "법인 인감 생성 API는 굳이 안써도 됩니다. / 법인 인감 생성 시 기존에 인감 지워진 인감이 있다면 사용 불가")
    public ResponseEntity<ResponseMessage<Object>> updateSeal(@RequestHeader("Authorization") String accessToken,
        @RequestPart(value = "sealImage") MultipartFile sealImage) {

        companyService.updateSeal(accessToken, sealImage);
        return ResponseEntity.ok(new ResponseMessage<>(200, "법인 인감 수정 성공", null));
    }

    @DeleteMapping("/master/company/seal")
    @Operation(summary = "법인 인감 삭제 API")
    public ResponseEntity<ResponseMessage<Object>> deleteSeal(@RequestHeader("Authorization") String accessToken) {

        companyService.deleteSeal(accessToken);
        return ResponseEntity.ok(new ResponseMessage<>(200, "법인 인감 삭제 성공", null));
    }

    @PostMapping("/member/my-pw")
    @Operation(summary = "회원정보 수정 전 내 비밀번호 확인 API. 비밀번호가 맞으면 200, 틀리면 400으로 HTTP 상태 코드가 전달됨")
    public ResponseEntity<ResponseMessage<String>> checkPassword(@RequestHeader("Authorization") String accessToken,
        @RequestBody CheckPwRequestDTO checkPasswordRequestDTO) {
        String checkNum = memberService.checkPassword(accessToken, checkPasswordRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "비밀번호가 확인되었습니다", checkNum));
    }

    @PutMapping("/member/my-pw")
    @Operation(summary = "내 비밀번호 변경 API(로그인한 유저 전용)")
    public ResponseEntity<ResponseMessage<Object>> changeMyPassword(@RequestHeader("Authorization") String accessToken,
        @RequestBody CheckPwRequestDTO checkPasswordRequestDTO) {
        memberService.changeMyPassword(accessToken, checkPasswordRequestDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "비밀번호가 변경되었습니다", null));
    }

    @PostMapping("/member/my-signature")
    @Operation(summary = "내 서명 생성 API")
    public ResponseEntity<ResponseMessage<Object>> createMySignature(@RequestHeader("Authorization") String accessToken,
        @RequestPart(value = "signatureImage") MultipartFile signatureImage,
        @RequestPart CheckNumDTO checkNumDTO) {
        memberService.createMySignature(accessToken, signatureImage, checkNumDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "서명이 생성되었습니다", null));
    }

    @PutMapping("/member/my-signature")
    @Operation(summary = "내 서명 변경 API")
    public ResponseEntity<ResponseMessage<Object>> changeMySignature(@RequestHeader("Authorization") String accessToken,
        @RequestPart(value = "signatureImage") MultipartFile signatureImage,
        @RequestPart CheckNumDTO checkNumDTO) {
        memberService.changeMySignature(accessToken, signatureImage, checkNumDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "서명이 변경되었습니다", null));
    }

    @DeleteMapping("/member/my-signature")
    @Operation(summary = "내 서명 삭제 API")
    public ResponseEntity<ResponseMessage<Object>> deleteMySignature(@RequestHeader("Authorization") String accessToken,
        @RequestBody CheckNumDTO checkNumDTO) {
        memberService.deleteMySignature(accessToken, checkNumDTO);
        return ResponseEntity.ok(new ResponseMessage<>(200, "서명이 삭제되었습니다", null));
    }

}
