package com.kh.workation.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
        // application.properties에 설정된 실제 uploads 폴더의 File 객체 생성
        File uploadDirectory = new File(uploadDir);
        
        // URI 형태로 자동 변환
        String uploadPath = uploadDirectory.toURI().toString();

        // /uploads/** URL 요청을 실제 로컬 uploads 폴더로 연결
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
		
	}
}
