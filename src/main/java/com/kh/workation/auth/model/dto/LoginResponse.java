package com.kh.workation.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

	@Schema(description = "JWT Access Token")
    private String accessToken;

	@Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

	@Schema(description = "로그인한 사용자의 권한", example = "SUPER")
    private String role;
}
