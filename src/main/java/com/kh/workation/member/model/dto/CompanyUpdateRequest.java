package com.kh.workation.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "고객사 수정 요청 DTO")
public class CompanyUpdateRequest {

    @Schema(description = "고객사명", example = "KH Company")
    private String companyName;

    @Schema(description = "사업자등록번호", example = "1234567890")
    private String businessNo;

    @Schema(description = "고객사 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
