package com.varc.brewnetapp.security.provider;

import com.varc.brewnetapp.security.utility.JwtUtil;
import com.varc.brewnetapp.security.utility.token.JwtAuthenticationAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * AccessTokenAuthenticationProvider는 JWT 액세스 토큰을 이용한 인증을 처리하는 AuthenticationProvider입니다.
 * <p>
 * 전달받은 액세스 토큰이 유효한지 검증하고, 유효할 경우 토큰에서 인증 정보를 추출하여 {@link Authentication} 객체로 반환합니다.
 * </p>
 */
@Slf4j
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;

    /**
     * AccessTokenAuthenticationProvider 생성자.
     *
     * @param jwtUtil JWT 토큰 검증 및 인증 정보 추출을 위한 JwtUtil
     */
    @Autowired
    public AccessTokenAuthenticationProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * JWT 액세스 토큰의 유효성을 검사하고, 인증 정보를 추출합니다.
     *
     * @param authentication 인증 요청 객체 (토큰을 Credentials로 포함)
     * @return 유효한 토큰의 경우, 해당 토큰에서 추출한 {@link Authentication} 객체
     * @throws AuthenticationException 토큰이 유효하지 않은 경우 예외 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        if (jwtUtil.isTokenValid(token)) {
            return jwtUtil.getAuthentication(token);
        } else {
            throw new IllegalArgumentException("invalid token");
        }
    }

    /**
     * 이 AuthenticationProvider가 주어진 인증 토큰 클래스를 지원하는지 확인합니다.
     *
     * @param authentication 인증 토큰 클래스
     * @return 주어진 클래스가 {@link JwtAuthenticationAccessToken}의 서브타입이면 {@code true} 반환
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationAccessToken.class.isAssignableFrom(authentication);
    }
}
