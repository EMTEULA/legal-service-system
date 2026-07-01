package com.legal.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.common.Result;
import com.legal.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public LoginInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            unauthorized(response, "请先登录");
            return false;
        }
        try {
            Claims claims = jwtUtil.parse(authorization.substring(7));
            request.setAttribute("userId", ((Number) claims.get("userId")).intValue());
            request.setAttribute("refId", ((Number) claims.get("refId")).intValue());
            request.setAttribute("role", claims.get("role", String.class));
            request.setAttribute("username", claims.get("username", String.class));
            return true;
        } catch (Exception e) {
            unauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(message)));
    }
}
