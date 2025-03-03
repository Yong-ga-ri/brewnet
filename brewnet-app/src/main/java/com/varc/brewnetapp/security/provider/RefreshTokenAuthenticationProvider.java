package com.varc.brewnetapp.security.provider;

import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.security.utility.JwtUtil;
import com.varc.brewnetapp.security.utility.token.JwtAuthenticationRefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenAuthenticationProvider는 JWT 리프레시 토큰을 이용한 인증을 처리하는 AuthenticationProvider입니다.
 * <p>
 * 리프레시 토큰의 유효성을 확인하고, Redis 등에 저장된 리프레시 토큰과 일치하는지 검증합니다.
 * 유효할 경우, 저장된 사용자 정보를 기반으로 새로운 액세스 토큰을 생성하고 HTTP 응답 헤더에 설정합니다.
 * </p>
 */
@Slf4j
@Component
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final HttpServletResponse response;

    /**
     * RefreshTokenAuthenticationProvider 생성자.
     *
     * @param refreshTokenService 리프레시 토큰의 저장 및 검증을 위한 RefreshTokenService
     * @param authService         사용자 정보를 로드하기 위한 AuthService
     * @param jwtUtil             JWT 토큰 생성 및 검증을 위한 JwtUtil
     * @param response            인증 성공 시 액세스 토큰을 헤더에 설정하기 위한 HttpServletResponse
     */
    @Autowired
    public RefreshTokenAuthenticationProvider(
            RefreshTokenService refreshTokenService,
            AuthService authService,
            JwtUtil jwtUtil,
            HttpServletResponse response
    ) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.response = response;
    }

    /**
     * JWT 리프레시 토큰의 유효성을 검사하고, Redis에 저장된 토큰과 비교한 후 새로운 액세스 토큰을 발급합니다.
     *
     * @param authentication 인증 요청 객체 (리프레시 토큰을 Credentials로 포함)
     * @return 유효한 토큰인 경우, {@link JwtUtil#getAuthentication(String)}을 통해 얻은 {@link Authentication} 객체 반환
     * @throws AuthenticationException 토큰이 유효하지 않거나, 검증에 실패한 경우 예외 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        String loginId = jwtUtil.getLoginId(token);
        if (refreshTokenService.checkRefreshTokenInRedis(loginId, token)) {
            if (jwtUtil.isTokenValid(token)) {
                try {
                    UserDetails savedUser = authService.loadUserByUsername(loginId);
                    Authentication authResult =  new UsernamePasswordAuthenticationToken(
                            savedUser,
                            savedUser.getPassword(),
                            savedUser.getAuthorities()
                    );
                    String accessToken = jwtUtil.generateAccessToken(authResult);
                    response.setHeader("Authorization", "Bearer " + accessToken);
                } catch (Exception e) {
                    throw new IllegalArgumentException("user not found", e);
                }
                return jwtUtil.getAuthentication(token);
            } else {
                throw new IllegalArgumentException("invalid token");
            }
        } else {
            throw new IllegalArgumentException("refresh token not found");
        }
    }

    /**
     * 이 AuthenticationProvider가 주어진 인증 토큰 클래스를 지원하는지 확인합니다.
     *
     * @param authentication 인증 토큰 클래스
     * @return 주어진 클래스가 {@link JwtAuthenticationRefreshToken}의 서브타입이면 {@code true} 반환
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationRefreshToken.class.isAssignableFrom(authentication);
    }
}
