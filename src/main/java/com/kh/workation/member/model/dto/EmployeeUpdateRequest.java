package com.kh.workation.member.model.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "직원 계정 수정 요청 DTO")
public class EmployeeUpdateRequest {

    @Schema(description = "로그인 아이디", example = "employee01")
    private String loginId;

    @Schema(description = "변경할 비밀번호, 미입력 시 기존 비밀번호 유지", example = "test!1234")
    private String password;

    @Schema(description = "사번", example = "1001")
    private Long empNo;

    @Schema(description = "직원 이름", example = "직원 01")
    private String employeeName;

    @Schema(description = "전화번호", example = "01010000001")
    private String phone;

    @Schema(description = "이메일", example = "employee01@dummy.com")
    private String email;

    @Schema(description = "부서", example = "개발팀")
    private String department;

    @Schema(description = "직급", example = "사원")
    private String position;

    @Schema(description = "워케이션 사용 가능 일수", example = "10")
    private Integer workationAvailDays;

    @Schema(description = "상태", example = "ACTIVE", allowableValues = {"ACTIVE", "LOCKED"})
    private String status;

    @Schema(description = "회원가입 처리 여부", example = "Y", allowableValues = {"Y", "N"})
    private String isProgressed;

    @Schema(description = "입사일", example = "2026-08-20")
    private LocalDate hireDate;

    @Schema(description = "퇴사일", example = "2026-12-31", nullable = true)
    private LocalDate resignDate;
}
