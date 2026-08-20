package com.kh.workation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	// 기본적인 설정은 OpenAPI 라는 클래스를 빈으로 등록하면서 셋팅하면 된다!!
	// > Swagger 문서 상단에 보여질 제목, 설명, 버전, 작성자 정보 등등
	@Bean
	public OpenAPI openAPI() {
		
		// "JWT" 방식의 인증방식 등록 (@SecurityRequirement(name="JWT") 와 맞춰야함)
		String jwt = "JWT";
		
		Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme().name(jwt)
																							 .type(SecurityScheme.Type.HTTP)
																							 .scheme("bearer")
																							 .bearerFormat("JWT"));
		// > Swagger 문서 상에서 테스트 시, 토큰이 필요한 경우 토큰값을 입력할 수 있는 창을 띄워주고,
		//   토큰값을 입력 후 Authorize 버튼을 클릭하면 내가 입력한 토큰값 앞에 "Bearer " 를 붙여서 테스트해준다.
		
		return new OpenAPI().info(new Info().title("Workation API")
											.description("Workation 서비스 API 명세서입니다.")
											.version("v1.0.0")
											.contact(new Contact().name("KH Workation")
											.email("workation@example.com")))
							.components(components);
	}
	
}
