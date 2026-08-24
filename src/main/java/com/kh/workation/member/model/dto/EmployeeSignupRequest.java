package com.kh.workation.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "직원 회원가입 신청 DTO")
public class EmployeeSignupRequest {

    @Schema(description = "사업자등록번호", example = "1111111111")
    private String businessNo;

    @Schema(description = "회사명", example = "더미 회사 1")
    private String companyName;

    @Schema(description = "직원 로그인 아이디", example = "employee11")
    private String loginId;

    @Schema(description = "비밀번호", example = "test!1234")
    private String password;

    @Schema(description = "사번", example = "1011")
    private Long empNo;

    @Schema(description = "직원 이름", example = "직원 11")
    private String employeeName;

    @Schema(description = "전화번호", example = "01010000011")
    private String phone;

    @Schema(description = "이메일", example = "employee11@dummy.com")
    private String email;

    @Schema(description = "부서명", example = "개발팀")
    private String department;

    @Schema(description = "직급", example = "사원")
    private String position;
}
