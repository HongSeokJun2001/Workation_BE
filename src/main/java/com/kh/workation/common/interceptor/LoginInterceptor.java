package com.kh.workation.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.workation.auth.model.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 로그인 사용자 전용 요청 시 유효한 로그인 토큰인지 검사해주는 인터셉터
@Component
public class LoginInterceptor implements HandlerInterceptor {

	private final AuthService authService;

	public LoginInterceptor(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			if (authService.isValidToken(token)) {
				return true;
			}
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false;
	}
}