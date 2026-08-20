package com.kh.workation.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.workation.auth.model.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 관리자 전용 요청 시 관리자가 맞는지 검사해주는 인터셉터
@Component // 이 클래스를 빈으로 등록하겠다.
public class AuthInterceptor implements HandlerInterceptor {

	private final AuthService authService;

	public AuthInterceptor(AuthService authService) {
		this.authService = authService;
	}

	// 요청 "전" 에 "관리자가 맞는지" 검증하겠다. (jwt 티켓 검사용 메소드)
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			if (authService.isAdminToken(token)) {
				String requestUri = request.getRequestURI();

				if (requestUri.contains("/admin/super-admin/")
						&& !authService.isSuperAdminToken(token)) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return false;
				}

				return true;
			}
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false;
	}

}

