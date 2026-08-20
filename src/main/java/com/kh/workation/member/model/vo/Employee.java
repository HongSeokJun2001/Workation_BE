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

@Schema(description="직원 정보 엔티티")

@Entity
@Table(name = "EMPLOYEE")

@DynamicInsert
@DynamicUpdate

@Getter
@Setter
@NoArgsConstructor
public class Employee {

	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_LOCKED = "LOCKED";
	public static final String PROGRESSED_Y = "Y";
	public static final String PROGRESSED_N = "N";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMPLOYEE_ID")
	@Schema(description = "직원 고유 번호", example = "1")
	private Long employeeId;

	@Column(name = "COMPANY_ID", nullable = false)
	@Schema(description = "소속 회사 번호", example = "1")
	private Long companyId;

	@Column(name = "LOGIN_ID", nullable = false, unique = true, length = 100)
	@Schema(description = "직원 로그인 아이디", example = "employee01")
	private String loginId;

	@Column(name = "PASSWORD", nullable = false, length = 255)
	@Schema(description = "BCrypt로 암호화된 직원 비밀번호", example = "$2a$10$exampleEncodedPassword")
	private String password;

	@Column(name = "EMP_NO", nullable = false)
	@Schema(description = "사번", example = "1001")
	private Long empNo;

	@Column(name = "EMPLOYEE_NAME", nullable = false, length = 100)
	@Schema(description = "직원 이름", example = "홍길동")
	private String employeeName;

	@Column(name = "PHONE", nullable = false, length = 20)
	@Schema(description = "전화번호", example = "01012345678")
	private String phone;

	@Column(name = "EMAIL", nullable = false, length = 100)
	@Schema(description = "이메일", example = "employee@example.com")
	private String email;

	@Column(name = "DEPARTMENT", length = 100)
	@Schema(description = "부서명", example = "개발팀", nullable = true)
	private String department;

	@Column(name = "POSITION", length = 100)
	@Schema(description = "직급", example = "대리", nullable = true)
	private String position;

	@Column(name = "WORKATION_AVAIL_DAYS", nullable = false)
	@Schema(description = "워케이션 사용 가능 일수", example = "0")
	private Integer workationAvailDays;

	@Column(name = "STATUS", nullable = false, length = 20)
	@Schema(description = "직원 계정 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "LOCKED"})
	private String status;

	@Column(name = "HIRE_DATE", nullable = false)
	@Schema(description = "입사일", example = "2026-08-20")
	private LocalDate hireDate;

	@Column(name = "RESIGN_DATE")
	@Schema(description = "퇴사일", example = "2026-12-31", nullable = true)
	private LocalDate resignDate;

	@Column(name = "IS_PROGRESSED", nullable = false, length = 20)
	@Schema(description = "회원가입 처리 여부", example = "N", allowableValues = {"Y", "N"})
	private String isProgressed;

	@PrePersist
	public void prePersist() {
		if (this.status == null || this.status.isBlank()) {
			this.status = STATUS_ACTIVE;
		}
		if (this.workationAvailDays == null) {
			this.workationAvailDays = 0;
		}
		if (this.hireDate == null) {
			this.hireDate = LocalDate.now();
		}
		if (this.isProgressed == null || this.isProgressed.isBlank()) {
			this.isProgressed = PROGRESSED_N;
		}
	}
}
