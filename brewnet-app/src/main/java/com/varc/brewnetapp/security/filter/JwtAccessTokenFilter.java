package com.varc.brewnetapp.security.filter;

import com.varc.brewnetapp.security.provider.ProviderManager;
import com.varc.brewnetapp.security.utility.token.JwtAuthenticationAccessToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAccessTokenFilter는 클라이언트 요청의 Authorization 헤더에 포함된 JWT 액세스 토큰을 검증하는 필터입니다. <br>
 * <p>
 * 요청 헤더에 "Bearer "로 시작하는 토큰이 있는 경우, {@link ProviderManager}를 사용해 인증을 수행하고, <br>
 * 인증에 성공하면 {@link SecurityContextHolder}에 인증 정보를 설정합니다.
 * </p>
 */
@Slf4j
@Component
public class JwtAccessTokenFilter extends OncePerRequestFilter {
    private final ProviderManager providerManager;

    /**
     * JwtAccessTokenFilter 생성자.
     *
     * @param providerManager JWT 액세스 토큰 인증 처리를 위한 ProviderManager
     */
    @Autowired
    public JwtAccessTokenFilter(ProviderManager providerManager) {
        this.providerManager = providerManager;
    }

    /**
     * HTTP 요청에서 Authorization 헤더를 확인하여 JWT 액세스 토큰을 검증합니다.
     *
     * @param request     HttpServletRequest 객체
     * @param response    HttpServletResponse 객체
     * @param filterChain 다음 필터로 요청을 전달하기 위한 FilterChain
     * @throws ServletException 서블릿 관련 예외 발생 시
     * @throws IOException      I/O 예외 발생 시
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Authentication authentication = providerManager.authenticate(new JwtAuthenticationAccessToken(token));
            if (authentication != null && authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
