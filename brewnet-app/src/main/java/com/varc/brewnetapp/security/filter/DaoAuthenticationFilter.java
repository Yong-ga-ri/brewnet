package com.varc.brewnetapp.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varc.brewnetapp.shared.ResponseMessage;
import com.varc.brewnetapp.security.provider.ProviderManager;
import com.varc.brewnetapp.security.vo.LoginRequestVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;


/**
 * DaoAuthenticationFilter는 사용자 로그인 요청을 처리하는 필터입니다.
 * <p>
 * 이 필터는 "/api/v1/auth/login" 경로로 들어오는 로그인 요청에 대해, <br>
 * 요청 본문에서 {@link LoginRequestVO} 객체를 파싱하고, <br>
 * {@link ProviderManager}를 이용하여 인증을 수행합니다. <br>
 * 인증에 성공하면, 성공 응답(JSON 메시지)을 반환합니다.
 * </p>
 */
@Slf4j
@Component
public class DaoAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ProviderManager providerManager;
    private final ObjectMapper objectMapper;

    /**
     * DaoAuthenticationFilter 생성자.
     *
     * @param providerManager  인증 처리를 위한 ProviderManager (AuthenticationManager 구현체)
     * @param objectMapper     JSON 파싱을 위한 ObjectMapper
     */
    @Autowired
    public DaoAuthenticationFilter(
            ProviderManager providerManager,
            ObjectMapper objectMapper
    ) {
        this.providerManager = providerManager;
        this.objectMapper = objectMapper;
        this.setFilterProcessesUrl("/api/v1/auth/login");
        super.setAuthenticationManager(providerManager);
    }

    /**
     * 로그인 요청의 인증 정보를 파싱하여 인증을 시도합니다.
     *
     * @param request   HttpServletRequest 객체 (로그인 요청 정보 포함)
     * @param response  HttpServletResponse 객체
     * @return 인증 결과 {@link Authentication} 객체
     * @throws AuthenticationException 인증 실패 시 예외 발생
     */
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequestVO loginRequestVO = objectMapper.readValue(request.getInputStream(), LoginRequestVO.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequestVO.getLoginId(),
                            loginRequestVO.getPassword(),
                            new ArrayList<>()
                    );
            return providerManager.authenticate(authToken);
        } catch (IOException e) {
            log.error("로그인 요청 파싱 실패", e);
            throw new IllegalArgumentException("로그인 요청 파싱 실패", e);
        }
    }

    /**
     * 인증 성공 시 호출되며, 성공 응답 메시지를 JSON 형태로 클라이언트에 반환합니다.
     *
     * @param request    HttpServletRequest 객체
     * @param response   HttpServletResponse 객체
     * @param chain      FilterChain 객체
     * @param authResult 인증 성공 결과 객체
     * @throws IOException 응답 작성 중 발생하는 IOException
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        ResponseMessage<String> responseMessage = new ResponseMessage<>(
                HttpServletResponse.SC_OK,
                "login successful",
                null
        );
        writeJsonResponse(response, responseMessage);
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
