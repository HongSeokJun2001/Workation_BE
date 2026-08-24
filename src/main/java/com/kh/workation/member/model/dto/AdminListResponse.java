package com.kh.workation.member.model.dto;

import com.kh.workation.member.model.vo.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "관리자 목록 응답 DTO")
public class AdminListResponse {

    @Schema(description = "관리자 고유 번호", example = "1")
    private Long adminId;

    @Schema(description = "회사 번호", example = "1", nullable = true)
    private Long companyId;

    @Schema(description = "회사명", example = "더미 회사 1", nullable = true)
    private String companyName;

    @Schema(description = "회사 표시값", example = "더미 회사 1(1)")
    private String companyLabel;

    @Schema(description = "로그인 아이디", example = "companyadmin01")
    private String loginId;

    @Schema(description = "권한", example = "COMPANY")
    private String role;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    public static AdminListResponse from(Admin admin, String companyName) {
        return AdminListResponse.builder()
                .adminId(admin.getAdminId())
                .companyId(admin.getCompanyId())
                .companyName(companyName)
                .companyLabel(createCompanyLabel(admin.getCompanyId(), companyName))
                .loginId(admin.getLoginId())
                .role(admin.getRole())
                .status(admin.getStatus())
                .build();
    }

    private static String createCompanyLabel(Long companyId, String companyName) {
        if (companyId == null) {
            return "-";
        }

        if (companyName == null || companyName.isBlank()) {
            return String.valueOf(companyId);
        }

        return companyName + "(" + companyId + ")";
    }
}
