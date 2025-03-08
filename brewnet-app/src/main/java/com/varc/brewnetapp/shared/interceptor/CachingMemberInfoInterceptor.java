package com.varc.brewnetapp.shared.interceptor;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class CachingMemberInfoInterceptor implements HandlerInterceptor {

    private final RefreshTokenService refreshTokenService;

    public CachingMemberInfoInterceptor(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginId = request.getAttribute("loginId").toString();
        MemberInfoDTO memberInfoDTO = refreshTokenService.getMemberInfoFromRedis(loginId);
        request.setAttribute("memberInfoDTO", memberInfoDTO);
        return true;
    }
}
