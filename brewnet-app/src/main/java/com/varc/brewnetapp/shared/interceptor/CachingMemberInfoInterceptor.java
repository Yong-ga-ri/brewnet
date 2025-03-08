package com.varc.brewnetapp.shared.interceptor;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class CachingMemberInfoInterceptor implements HandlerInterceptor {
    public static final String SERVLET_ATTRIBUTE_MEMBER_INFO_KEY = "memberInfoDTO";

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public CachingMemberInfoInterceptor(RefreshTokenService refreshTokenService, AuthService authService) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginId = request.getAttribute("loginId").toString();
        MemberInfoDTO memberInfoDTO = refreshTokenService.getMemberInfoFromRedis(loginId);
        if (memberInfoDTO == null) memberInfoDTO = authService.getMemberInfoDTOBy(loginId);
        request.setAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY, memberInfoDTO);
        return true;
    }
}
