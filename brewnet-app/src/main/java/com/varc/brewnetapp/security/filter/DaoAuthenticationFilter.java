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

@Slf4j
@Component
public class DaoAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ProviderManager providerManager;
    private final ObjectMapper objectMapper;

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

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequestVO loginRequestVO = objectMapper.readValue(request.getInputStream(), LoginRequestVO.class);
            return providerManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestVO.getLoginId(), loginRequestVO.getPassword(), new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        final ResponseMessage<String> responseMessage = new ResponseMessage<>(
                HttpServletResponse.SC_OK,
                "login successful",
                null
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final String jsonResponse = objectMapper.writeValueAsString(responseMessage);
        response.getWriter().write(jsonResponse);
    }
}
