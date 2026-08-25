package com.kh.workation.member.model.dto;

import java.time.LocalDate;

import com.kh.workation.member.model.vo.Employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "직원 상세 응답 DTO")
public class EmployeeDetailResponse {

    @Schema(description = "회사 번호", example = "1")
    private Long companyId;

    @Schema(description = "회사명", example = "더미 회사 1")
    private String companyName;

    @Schema(description = "회사 표시값", example = "더미 회사 1(1)")
    private String companyLabel;

    @Schema(description = "직원 로그인 아이디", example = "employee01")
    private String loginId;

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

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "입사일", example = "2026-08-20")
    private LocalDate hireDate;

    @Schema(description = "퇴사일", example = "2026-12-31", nullable = true)
    private LocalDate resignDate;

    @Schema(description = "회원가입 처리 여부", example = "N")
    private String isProgressed;

    public static EmployeeDetailResponse from(Employee employee, String companyName) {
        return EmployeeDetailResponse.builder()
                .companyId(employee.getCompanyId())
                .companyName(companyName)
                .companyLabel(createCompanyLabel(employee.getCompanyId(), companyName))
                .loginId(employee.getLoginId())
                .empNo(employee.getEmpNo())
                .employeeName(employee.getEmployeeName())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .workationAvailDays(employee.getWorkationAvailDays())
                .status(employee.getStatus())
                .hireDate(employee.getHireDate())
                .resignDate(employee.getResignDate())
                .isProgressed(employee.getIsProgressed())
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
