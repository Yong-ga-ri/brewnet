package com.varc.brewnetapp.security.provider;

import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.security.utility.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DaoAuthenticationProvider는 DAO 기반 사용자 인증을 처리하는 AuthenticationProvider입니다.
 * <p>
 * 사용자 로그인 시 입력받은 로그인 ID와 비밀번호를 사용하여, 저장된 사용자 정보와 비교하고,
 * 인증 성공 시 JWT 토큰(액세스 토큰 및 리프레시 토큰)을 생성하여 HTTP 응답 헤더에 설정합니다.
 * </p>
 */
@Slf4j
@Component
public class DaoAuthenticationProvider implements AuthenticationProvider {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final HttpServletResponse response;

    /**
     * DaoAuthenticationProvider 생성자.
     *
     * @param authService           사용자 정보를 로드하기 위한 AuthService
     * @param refreshTokenService   리프레시 토큰 저장 및 검증을 위한 RefreshTokenService
     * @param bCryptPasswordEncoder 비밀번호 암호화를 위한 BCryptPasswordEncoder
     * @param jwtUtil               JWT 토큰 생성 및 검증을 위한 JwtUtil
     * @param response              인증 성공 시 토큰을 헤더에 설정하기 위한 HttpServletResponse
     */
    @Autowired
    public DaoAuthenticationProvider(
            AuthService authService,
            RefreshTokenService refreshTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtUtil jwtUtil,
            HttpServletResponse response
    ) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.response = response;
    }

    /**
     * 사용자의 로그인 ID와 비밀번호를 기반으로 인증을 수행합니다.
     * <p>
     * 저장된 사용자 정보와 입력된 비밀번호를 비교하고, 일치할 경우 JWT 액세스 및 리프레시 토큰을 생성하여 HTTP 응답 헤더에 설정합니다.
     * </p>
     *
     * @param authentication 인증 요청 객체 (로그인 ID와 비밀번호 포함)
     * @return 인증 성공 시, 인증된 {@link Authentication} 객체
     * @throws AuthenticationException 인증 실패 시 예외 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getPrincipal().toString();
        String loginPassword = authentication.getCredentials().toString();

        UserDetails savedUser = authService.loadUserByUsername((authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName());

        if (!bCryptPasswordEncoder.matches(loginPassword, savedUser.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        } else {
            Authentication authenticationResult = new UsernamePasswordAuthenticationToken(savedUser, savedUser.getPassword(), savedUser.getAuthorities());

            String refreshToken = jwtUtil.generateRefreshToken(authenticationResult);
            String accessToken = jwtUtil.generateAccessToken(authenticationResult);

            refreshTokenService.saveRefreshToken(loginId, refreshToken);
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh-Token", refreshToken);

            return authenticationResult;
        }

    }

    /**
     * 이 AuthenticationProvider가 주어진 인증 토큰 클래스를 지원하는지 확인합니다.
     *
     * @param authentication 인증 토큰 클래스
     * @return 주어진 클래스가 {@link UsernamePasswordAuthenticationToken}의 서브타입이면 {@code true} 반환
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
