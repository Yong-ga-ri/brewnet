package com.varc.brewnetapp.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varc.brewnetapp.shared.ResponseMessage;
import com.varc.brewnetapp.security.provider.ProviderManager;
import com.varc.brewnetapp.security.utility.token.JwtAuthenticationRefreshToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtRefreshTokenFilter는 클라이언트의 리프레시 토큰 요청을 처리하는 필터입니다.
 * <p>
 * 이 필터는 "/api/v1/auth/refresh" 경로와 POST 메서드에 해당하는 요청을 가로채,
 * 요청 헤더에서 "Refresh-Token" 값을 추출하여 {@link ProviderManager}를 통해 인증을 수행합니다.
 * 인증에 성공하면, 리프레시 토큰을 기반으로 새로운 액세스 토큰 갱신 성공 메시지를 JSON 형태로 반환합니다.
 * </p>
 */
@Slf4j
@Component
public class JwtRefreshTokenFilter extends OncePerRequestFilter {
    private final ProviderManager providerManager;
    private final ObjectMapper objectMapper;
    private final AntPathRequestMatcher refreshTokenRequestMatcher;

    /**
     * JwtRefreshTokenFilter 생성자.
     *
     * @param providerManager JWT 리프레시 토큰 인증 처리를 위한 ProviderManager
     * @param objectMapper    JSON 파싱 및 생성에 사용되는 ObjectMapper
     */
    @Autowired
    public JwtRefreshTokenFilter(
            ProviderManager providerManager,
            ObjectMapper objectMapper
    ) {
        this.providerManager = providerManager;
        this.objectMapper = objectMapper;
        this.refreshTokenRequestMatcher = new AntPathRequestMatcher("/api/v1/auth/refresh", "POST");
    }

    /**
     * HTTP 요청을 가로채어, 리프레시 토큰이 포함된 요청인 경우 JWT 리프레시 토큰을 인증하고 응답을 반환합니다.
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
        if (refreshTokenRequestMatcher.matches(request)) {
            String refreshToken = request.getHeader("Refresh-Token");

            if (refreshToken != null && !refreshToken.isEmpty()) {
                providerManager.authenticate(new JwtAuthenticationRefreshToken(refreshToken));
                ResponseMessage<String> responseMessage = new ResponseMessage<>(
                        HttpServletResponse.SC_OK,
                        "access token refreshed successfully",
                        null
                );
                writeJsonResponse(response, responseMessage);
                return;
            } else {
                log.warn("리프레시 토큰 헤더가 존재하지 않습니다.");
            }
        }
        filterChain.doFilter(request, response);

    }

    /**
     * 응답 객체에 JSON 형태의 메시지를 작성합니다.
     *
     * @param response        HttpServletResponse
     * @param responseMessage 응답에 사용할 메시지 객체
     * @throws IOException JSON 작성 중 발생하는 예외
     */
    private void writeJsonResponse(HttpServletResponse response, ResponseMessage<String> responseMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final String jsonResponse = objectMapper.writeValueAsString(responseMessage);
        response.getWriter().write(jsonResponse);
    }
}
