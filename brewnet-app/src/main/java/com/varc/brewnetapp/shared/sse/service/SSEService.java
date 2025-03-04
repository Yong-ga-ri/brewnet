package com.varc.brewnetapp.shared.sse.service;

import com.varc.brewnetapp.domain.franchise.command.domain.aggregate.entity.FranchiseMember;
import com.varc.brewnetapp.domain.franchise.command.domain.repository.FranchiseMemberRepository;
import com.varc.brewnetapp.domain.member.command.domain.aggregate.entity.Member;
import com.varc.brewnetapp.domain.member.command.domain.repository.MemberRepository;
import com.varc.brewnetapp.shared.sse.dto.RedisAlarmDTO;
import com.varc.brewnetapp.shared.sse.repository.FailedAlarmRepository;
import com.varc.brewnetapp.shared.sse.repository.SSERepository;
import com.varc.brewnetapp.shared.exception.MemberNotFoundException;
import com.varc.brewnetapp.security.utility.JwtUtil;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class SSEService {

    private final SSERepository sseRepository;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final FailedAlarmRepository failedAlarmRepository;
    private final FranchiseMemberRepository franchiseMemberRepository;

    @Autowired
    public SSEService(SSERepository sseRepository, JwtUtil jwtUtil,
        MemberRepository memberRepository,
        FailedAlarmRepository failedAlarmRepository,
        FranchiseMemberRepository franchiseMemberRepository) {
        this.sseRepository = sseRepository;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.failedAlarmRepository = failedAlarmRepository;
        this.franchiseMemberRepository = franchiseMemberRepository;
    }

    /**
     * 클라이언트의 이벤트 구독을 허용하는 메서드
     */
    public SseEmitter subscribe(String accessToken) {
        String senderLoginId = jwtUtil.getLoginId(accessToken.replace("Bearer ", ""));
        Integer memberCode = memberRepository.findById(senderLoginId)
            .orElseThrow(() -> new MemberNotFoundException("해당 토큰으로 회원 정보를 조회할 수 없습니다"))
            .getMemberCode();

        // sse의 유효 시간이 만료되면, 클라이언트에서 다시 서버로 이벤트 구독을 시도한다.
        SseEmitter sseEmitter = sseRepository.save(memberCode, new SseEmitter(60L * 1000 * 60));

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> sseRepository.deleteById(memberCode));

        // Emitter의 유효 시간이 만료되면 emitter 삭제
        sseEmitter.onTimeout(() -> sseRepository.deleteById(memberCode));

        // 첫 구독시에 이벤트를 발생시킨다.
        sendToMember(null, "SSE 구독 시작", memberCode, "Start Subscribe event, memberCode : " + memberCode);

        // 저장된 알람 전송
        List<RedisAlarmDTO> failedAlarms = failedAlarmRepository.getFailedAlarms(memberCode);
        failedAlarms.forEach(alarmDTO ->
                sendToMember(alarmDTO.getSenderMemberCode(),
                        "Past",
                        memberCode,
                        alarmDTO.getMessage())
        );

        // 전송 후 삭제
        failedAlarmRepository.clearFailedAlarms(memberCode);

        return sseEmitter;
    }

    /** 특정 회원 1명에게 알림 발송하는 method */
    public void sendToMember(Integer senderMemberCode, String eventName, Integer recipientMemberCode, String data) {
        if (isAlarmNotFromMe(senderMemberCode, recipientMemberCode)) {
            processAlarmForRecipient(senderMemberCode, recipientMemberCode, eventName, data);
        }
    }

    /**
     * 모든 활성 가맹점 회원에게 알림을 전송합니다.
     *
     * @param senderMemberCode 전송자 회원 코드
     * @param eventName        이벤트 이름
     * @param data             전송할 데이터 (null이면 기본 메시지 사용)
     */
    public void sendToFranchise(Integer senderMemberCode, String eventName, String data) {
        List<FranchiseMember> franchiseMembers = franchiseMemberRepository.findByActiveTrue();
        final String finalData = (data == null)
                ? generateDefaultMessage(senderMemberCode, "가맹점", eventName)
                : data;

        franchiseMembers.forEach(member -> {
            int memberCode = member.getMemberCode();
            if (isAlarmNotFromMe(senderMemberCode, memberCode)) {
                processAlarmForRecipient(senderMemberCode, memberCode, eventName, finalData);
            }
        });
    }

    /**
     * 특정 가맹점에 속한 회원들에게 알림을 전송합니다.
     *
     * @param senderMemberCode 전송자 회원 코드
     * @param franchiseCode    대상 가맹점 코드
     * @param eventName        이벤트 이름
     * @param data             전송할 데이터 (null이면 기본 메시지 사용)
     */
    public void sendToFranchiseBelongTo(Integer senderMemberCode, Integer franchiseCode, String eventName, String data) {
        List<FranchiseMember> franchiseMemberList = franchiseMemberRepository.findByFranchiseCodeAndActiveTrue(franchiseCode);
        final String finalData = (data == null)
                ? generateDefaultMessage(senderMemberCode, "가맹점", eventName)
                : data;

        franchiseMemberList.forEach(member -> {
            int memberCode = member.getMemberCode();
            if (isAlarmNotFromMe(senderMemberCode, memberCode)) {
                processAlarmForRecipient(senderMemberCode, memberCode, eventName, finalData);
            }
        });

    }

    /** 본사 유저들에게 알림 발송하는 method */
    public void sendToHq(Integer senderMemberCode, String eventName, String data) {
        List<Member> memberList = memberRepository.findByActiveTrueAndPositionCodeIsNotNull();
        final String finalData = (data == null)
                ? generateDefaultMessage(senderMemberCode, "본사", eventName)
                : data;

        memberList.forEach(member -> {
            int memberCode = member.getMemberCode();
            if (isAlarmNotFromMe(senderMemberCode, memberCode)) {
                processAlarmForRecipient(senderMemberCode, memberCode, eventName, finalData);
            }
        });
    }

    /**
     * 지정된 수신자에게 알림 전송을 위한 공통 로직.
     * <p>
     * SSE 연결 여부를 확인하고, 연결이 되어 있으면 알림을 전송합니다.
     * 연결이 되어 있지 않으면 실패 알람 저장소에 알림을 저장합니다.
     * </p>
     *
     * @param senderMemberCode   전송자 회원 코드 (null일 경우 기본 메시지 생성)
     * @param recipientMemberCode 수신자 회원 코드
     * @param eventName          이벤트 이름
     * @param data               전송할 데이터
     */
    private void processAlarmForRecipient(
            Integer senderMemberCode,
            int recipientMemberCode,
            String eventName,
            String data) {
        SseEmitter sseEmitter = getSseEmitter(recipientMemberCode);

        if (data == null) {
            data = (senderMemberCode != null ? senderMemberCode + "번 회원이 " : "") +
                    recipientMemberCode + "번 회원에게 " + eventName + "알람을 발송하였습니다";
        }
        RedisAlarmDTO alarmDTO = new RedisAlarmDTO(data, eventName, senderMemberCode);
        if (sseEmitter == null) {
            saveFailedAlarm(recipientMemberCode, alarmDTO);
        } else {
            sendAlarm(sseEmitter, recipientMemberCode, eventName, data, alarmDTO);
        }
    }

    /**
     * 특정 회원의 SSE Emitter를 반환합니다.
     *
     * @param memberCode 회원 코드
     * @return 해당 회원의 SseEmitter
     */
    private SseEmitter getSseEmitter(int memberCode) {
        return sseRepository.findById(memberCode);
    }

    /**
     * SSE 연결이 되어 있지 않으면 실패한 알람을 저장합니다.
     *
     * @param memberCode 회원 코드
     * @param alarmDTO      전송할 알람 정보
     */
    private void saveFailedAlarm(int memberCode, RedisAlarmDTO alarmDTO) {
        failedAlarmRepository.saveFailedAlarm(memberCode, alarmDTO);
    }

    /**
     * 주어진 SseEmitter를 이용하여 알림을 전송합니다.
     * <p>
     * 전송 중 IOException이 발생하면 실패 알람을 저장하고, 해당 Emitter를 삭제합니다.
     * </p>
     *
     * @param sseEmitter         전송에 사용할 SseEmitter
     * @param recipientMemberCode 수신자 회원 코드
     * @param eventName          이벤트 이름
     * @param data               전송할 데이터
     * @param alarmDTO              RedisAlarmDTO 알람 객체
     */
    private void sendAlarm(SseEmitter sseEmitter, int recipientMemberCode, String eventName, String data, RedisAlarmDTO alarmDTO) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(String.valueOf(recipientMemberCode))
                    .name(eventName)
                    .data(data));
        } catch (IOException ex) {
            log.error("SSE 알림 전송 실패 for memberCode {}", recipientMemberCode, ex);
            failedAlarmRepository.saveFailedAlarm(recipientMemberCode, alarmDTO);
            sseRepository.deleteById(recipientMemberCode);
        }
    }

    /**
     * 전송자와 수신자가 동일한 경우 알람 전송을 방지합니다.
     *
     * @param senderMemberCode 전송자 회원 코드
     * @param receiverMemberCode 수신자 회원 코드
     * @return 전송자가 수신자와 다르면 {@code true} 반환
     */
    private boolean isAlarmNotFromMe(Integer senderMemberCode, int receiverMemberCode) {
        return senderMemberCode == null || !Objects.equals(senderMemberCode, receiverMemberCode);
    }

    /**
     * data가 null일 경우 기본 메시지를 생성합니다.
     *
     * @param senderMemberCode 전송자 회원 코드
     * @param targetType       대상 (예: "가맹점", "본사")
     * @param eventName        이벤트 이름
     * @return 기본 메시지
     */
    private String generateDefaultMessage(Integer senderMemberCode, String targetType, String eventName) {
        return senderMemberCode + "번 회원이 " + targetType + " 회원들에게 " + eventName + "알람을 발송하였습니다";
    }
}
