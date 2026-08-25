package com.kh.workation.config;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		// 1. C드라이브 실제 uploads 폴더의 File 객체 생성
        File uploadDir = new File("C:/Final_Project/Workation_BE/uploads/");
        
        // 2. URI 형태(file:///C:/Final_Project/Workation_BE/uploads/)로 자동 변환
        String uploadPath = uploadDir.toURI().toString();

        // /uploads/** URL 요청을 실제 로컬 uploads 폴더로 연결
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
		
	}
}
