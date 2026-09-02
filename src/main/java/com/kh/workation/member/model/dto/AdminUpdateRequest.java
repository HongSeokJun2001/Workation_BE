package com.kh.workation.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "관리자 계정 수정 요청 DTO")
public class AdminUpdateRequest {

    @Schema(description = "소속 회사 번호. 최고관리자가 본사관리자 계정을 수정할 때만 변경 가능", example = "1", nullable = true)
    private Long companyId;

    @Schema(description = "로그인 아이디", example = "companyadmin01")
    private String loginId;

    @Schema(description = "변경할 비밀번호, 미입력 시 기존 비밀번호 유지", example = "test!1234")
    private String password;

    @Schema(description = "상태", example = "ACTIVE", allowableValues = {"ACTIVE", "LOCKED"})
    private String status;
}
