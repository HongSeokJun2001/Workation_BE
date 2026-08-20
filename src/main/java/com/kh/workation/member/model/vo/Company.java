package com.kh.workation.member.model.vo;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description="고객사 정보 엔티티")
@Entity
@Table(name = "COMPANY")

@DynamicInsert
@DynamicUpdate

@Getter
@Setter
@NoArgsConstructor
public class Company {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_ID")
    @Schema(description = "고객사 고유 번호", example = "1")
    private Long companyId;

    @Column(name = "COMPANY_NAME", nullable = false, length = 100)
    @Schema(description = "고객사명", example = "KH Company")
    private String companyName;

    @Column(name = "BUSINESS_NO", nullable = false, unique = true, length = 20)
    @Schema(description = "사업자등록번호", example = "1234567890")
    private String businessNo;

    @Column(name = "COMPANY_STATUS", nullable = false, length = 20)
    @Schema(description = "고객사 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String companyStatus;

    @Column(name = "CREATED_DATE", nullable = false)
    @Schema(description = "고객사 생성일", example = "2026-08-20")
    private LocalDate createdDate;

    @PrePersist
    public void prePersist() {
        if (this.companyStatus == null || this.companyStatus.isBlank()) {
            this.companyStatus = STATUS_ACTIVE;
        }
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();
        }
    }
}
