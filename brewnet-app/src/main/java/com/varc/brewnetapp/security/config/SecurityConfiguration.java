package com.varc.brewnetapp.security.config;

import com.varc.brewnetapp.security.filter.DaoAuthenticationFilter;
import com.varc.brewnetapp.security.filter.JwtAccessTokenFilter;
import com.varc.brewnetapp.security.filter.JwtRefreshTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfiguration 클래스는 애플리케이션의 Spring Security 설정을 담당합니다.
 * <p>
 * 이 구성은 CSRF, 세션, 폼 로그인 기능을 비활성화하고 JWT 기반의 인증 방식을 사용하도록 설정합니다. <br>
 * 또한, URL별 접근 제어를 설정하고, 커스텀 보안 필터들을 필터 체인에 등록합니다.
 * </p>
 *
 * <p>
 * URL 접근 제어는 다음과 같이 구성됩니다:
 * <ul>
 *   <li>다음 경로는 모두 접근 허용: "/", "/api/v1/check/**", "/api/v1/auth/**",
 *       "/api/v1/email/**", "/swagger-ui/**", "/v3/api-docs/**"</li>
 *   <li>"/api/v1/franchise/**": {@code FRANCHISE} 또는 {@code MASTER} 역할이 필요</li>
 *   <li>"/api/v1/hq/**": {@code GENERAL_ADMIN}, {@code RESPONSIBLE_ADMIN}, 또는 {@code MASTER} 역할이 필요</li>
 *   <li>"/api/v1/super/**": {@code RESPONSIBLE_ADMIN} 또는 {@code MASTER} 역할이 필요</li>
 *   <li>"/api/v1/responsible/**": {@code RESPONSIBLE_ADMIN} 또는 {@code MASTER} 역할이 필요</li>
 *   <li>"/api/v1/master/**", "/api/v1/require-auth/master": {@code MASTER} 역할이 필요</li>
 *   <li>"/api/v1/delivery": {@code DELIVERY} 또는 {@code MASTER} 역할이 필요</li>
 *   <li>그 외 모든 요청은 인증이 필요함</li>
 * </ul>
 * </p>
 *
 * <p>
 * 등록된 커스텀 필터:
 * <ul>
 *   <li>{@link JwtAccessTokenFilter}: JWT 액세스 토큰을 이용한 인증 처리</li>
 *   <li>{@link JwtRefreshTokenFilter}: JWT 리프레시 토큰을 이용한 인증 처리</li>
 *   <li>{@link DaoAuthenticationFilter}: DAO 기반 로그인 인증 처리</li>
 * </ul>
 * </p>
 *
 * <p>
 * 또한, 커스텀 {@code AuthenticationEntryPoint}와 {@code AccessDeniedHandler}를 이용해
 * 인증 실패(401)와 권한 부족(403) 상황을 처리합니다.
 * </p>
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String ROLE_MASTER = "MASTER";
    private static final String ROLE_FRANCHISE = "FRANCHISE";
    private static final String ROLE_GENERAL_ADMIN = "GENERAL_ADMIN";
    private static final String ROLE_RESPONSIBLE_ADMIN = "RESPONSIBLE_ADMIN";
    private static final String ROLE_DELIVERY = "DELIVERY";

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final JwtAccessTokenFilter jwtAccessTokenFilter;
    private final JwtRefreshTokenFilter jwtRefreshTokenFilter;
    private final DaoAuthenticationFilter daoAuthenticationFilter;

    /**
     * SecurityConfiguration 생성자.
     *
     * @param authenticationEntryPoint 커스텀 인증 실패 엔트리 포인트
     * @param accessDeniedHandler      커스텀 접근 거부 핸들러
     * @param jwtAccessTokenFilter     JWT 액세스 토큰 필터
     * @param jwtRefreshTokenFilter    JWT 리프레시 토큰 필터
     * @param daoAuthenticationFilter  DAO 기반 인증 필터
     */
    @Autowired
    public SecurityConfiguration(
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            JwtAccessTokenFilter jwtAccessTokenFilter,
            JwtRefreshTokenFilter jwtRefreshTokenFilter,
            DaoAuthenticationFilter daoAuthenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtAccessTokenFilter = jwtAccessTokenFilter;
        this.jwtRefreshTokenFilter = jwtRefreshTokenFilter;
        this.daoAuthenticationFilter = daoAuthenticationFilter;
    }

    /**
     * SecurityFilterChain을 구성하여 Spring Security 필터 체인을 반환합니다.
     *
     * <p>
     * 이 메서드는 다음과 같이 구성됩니다:
     * <ul>
     *   <li>CSRF 보호, 세션 관리, 폼 로그인을 비활성화하여 JWT 기반 인증 방식을 사용</li>
     *   <li>URL별 접근 권한을 설정</li>
     *   <li>커스텀 {@link JwtAccessTokenFilter}, {@link JwtRefreshTokenFilter}, {@link DaoAuthenticationFilter}를
     *       필터 체인에 등록</li>
     *   <li>커스텀 예외 처리 핸들러를 등록하여 인증 실패 및 권한 부족 상황 처리</li>
     * </ul>
     * </p>
     *
     * @param http HttpSecurity 인스턴스
     * @return 구성된 SecurityFilterChain
     * @throws Exception 구성이 실패할 경우 발생
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/api/v1/check/**",
                                "/api/v1/auth/**",
                                "/api/v1/email/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 가맹점
                        .requestMatchers("/api/v1/franchise/**").hasAnyRole(ROLE_FRANCHISE, ROLE_MASTER)

                        // 본사
                        .requestMatchers("/api/v1/hq/**").hasAnyRole(ROLE_GENERAL_ADMIN, ROLE_RESPONSIBLE_ADMIN, ROLE_MASTER)

                        // 본사책임자
                        .requestMatchers("/api/v1/super/**").hasAnyRole(ROLE_RESPONSIBLE_ADMIN, ROLE_MASTER)

                        // 본사책임자
                        .requestMatchers("/api/v1/responsible/**").hasAnyRole(ROLE_RESPONSIBLE_ADMIN, ROLE_MASTER)

                        // 마스터
                        .requestMatchers("/api/v1/master/**", "/api/v1/require-auth/master").hasRole(ROLE_MASTER)

                        // 마스터
                        .requestMatchers("/api/v1/delivery").hasAnyRole(ROLE_DELIVERY, ROLE_MASTER)

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .addFilterBefore(jwtAccessTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(daoAuthenticationFilter);

        return http.build();
    }

}
