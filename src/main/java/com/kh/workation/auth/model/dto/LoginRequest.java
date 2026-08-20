package com.kh.workation.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

	@Schema(description = "로그인 아이디", example = "admin")
    private String loginId;

	@Schema(description = "로그인 비밀번호", example = "test")
    private String password;

	@Schema(description = "로그인 유형", example = "ADMIN", allowableValues = {"ADMIN", "EMPLOYEE"})
    private String loginType;
}
