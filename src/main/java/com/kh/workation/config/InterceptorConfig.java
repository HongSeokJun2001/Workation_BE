package com.kh.workation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.workation.common.interceptor.AuthInterceptor;
import com.kh.workation.common.interceptor.LoginInterceptor;

@Configuration // 이 클래스를 환경설정 용도로 쓰겠다.
public class InterceptorConfig implements WebMvcConfigurer {

	@Autowired
	private AuthInterceptor authInterceptor;
	// > 관리자 요청이 들어왔을 때 preHandle 메소드가 호출될 수 있도록 AuthInterceptor 객체를 주입받는다.

	@Autowired
	private LoginInterceptor loginInterceptor;
	// > 로그인 사용자용 요청이 들어왔을 때 preHandle 메소드가 호출될 수 있도록 LoginInterceptor 객체를 주입받는다.

	// 요청이 AuthInterceptor 를 거쳐가도록 연결해주기
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// 로그인 사용자 전용 기능이 생기면 해당 경로를 명시적으로 등록한다.
		registry.addInterceptor(loginInterceptor)
		        .addPathPatterns("/lobby/**")
		        .excludePathPatterns("/auth/**", "/public/**", "/admin/**");
		
		registry.addInterceptor(authInterceptor)
			    .addPathPatterns("/admin/**")
			    .excludePathPatterns("/auth/**", "/public/**"); 
		// > "/admin" 계열의 모든 요청은 (/**)
		//   이 AuthInterceptor 를 거쳐가고,
		//   "/auth" 와 "/public" 계열의 요청은 피해가겠다.
		
		// "/admin" 계열의 요청은 아무나 못들어오게 막겠다.
		// > "블랙리스트"
		
		// "/auth", "/public" 계열의 요청은 아무나 들어올 수 있도록 풀겠다.
		// > "화이트리스트"
	}
	
}




