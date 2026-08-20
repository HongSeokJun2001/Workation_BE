package com.kh.workation.member.model.vo;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="관리자 정보 엔티티")
@Entity
@Table(name = "ADMIN")

@DynamicInsert
@DynamicUpdate

@Getter
@Setter
@NoArgsConstructor
public class Admin {

	public static final String ROLE_SUPER_ADMIN = "SUPER";
	public static final String ROLE_COMPANY_ADMIN = "COMPANY";
	public static final String ROLE_EMPLOYEE = "EMPLOYEE";
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_LOCKED = "LOCKED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ADMIN_ID")
	@Schema(description = "관리자 고유 번호", example = "1")
	private Long adminId;

	@Column(name = "COMPANY_ID")
	@Schema(description = "소속 회사 번호, 최고관리자는 null 가능", example = "1", nullable = true)
	private Long companyId;

	@Column(name = "LOGIN_ID", nullable = false, length = 100)
	@Schema(description = "관리자 로그인 아이디", example = "admin")
	private String loginId;

	@Column(name = "PASSWORD", nullable = false, length = 255)
	@Schema(description = "BCrypt로 암호화된 관리자 비밀번호", example = "$2a$10$exampleEncodedPassword")
	private String password;

	@Column(name = "ROLE", nullable = false, length = 20)
	@Schema(description = "관리자 권한", example = "SUPER", allowableValues = {"SUPER", "COMPANY"})
	private String role;

	@Column(name = "STATUS", nullable = false, length = 20)
	@Schema(description = "관리자 계정 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "LOCKED"})
	private String status;

	@PrePersist
	public void prePersist() {
		if (this.status == null || this.status.isBlank()) {
			this.status = STATUS_ACTIVE;
		}
		if (this.role == null || this.role.isBlank()) {
			this.role = ROLE_SUPER_ADMIN;
		}
	}

}
