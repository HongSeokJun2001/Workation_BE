package com.kh.workation.member.model.dto;

import java.time.LocalDate;

import com.kh.workation.member.model.vo.Company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "고객사 응답 DTO")
public class CompanyResponse {

    @Schema(description = "고객사 고유 번호", example = "1")
    private Long id;

    @Schema(description = "고객사명", example = "KH Company")
    private String companyName;

    @Schema(description = "사업자등록번호", example = "1234567890")
    private String businessNo;

    @Schema(description = "고객사 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Schema(description = "고객사 생성일", example = "2026-08-20")
    private LocalDate createdDate;

    public static CompanyResponse from(Company company) {
        return CompanyResponse.builder()
                .id(company.getCompanyId())
                .companyName(company.getCompanyName())
                .businessNo(company.getBusinessNo())
                .status(company.getCompanyStatus())
                .createdDate(company.getCreatedDate())
                .build();
    }
}
