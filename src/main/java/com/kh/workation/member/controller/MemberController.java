package com.kh.workation.member.controller;
	
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.member.model.dto.AdminDetailResponse;
import com.kh.workation.member.model.dto.AdminListResponse;
import com.kh.workation.member.model.dto.AdminUpdateRequest;
import com.kh.workation.member.model.dto.EmployeeDetailResponse;
import com.kh.workation.member.model.dto.EmployeeFindIdRequest;
import com.kh.workation.member.model.dto.EmployeeFindPasswordRequest;
import com.kh.workation.member.model.dto.EmployeePasswordResetRequest;
import com.kh.workation.member.model.dto.EmployeeRecoveryVerifyRequest;
import com.kh.workation.member.model.dto.EmployeeSignupRequest;
import com.kh.workation.member.model.dto.EmployeeUpdateRequest;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.member.model.service.EmployeeAccountRecoveryService;
import com.kh.workation.member.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name="Member API", description="관리자 및 직원 관련 API")

@CrossOrigin
@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private AuthService authService;

	@Autowired
	private EmployeeAccountRecoveryService employeeAccountRecoveryService;

	@Operation(summary="최고관리자 및 본사관리자 목록 조회", description="ADMIN 테이블에서 최고관리자와 본사관리자를 함께 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/super/member/list")
	public ResponseEntity<List<AdminListResponse>> selectAdminList(
			@RequestParam(defaultValue = "ALL") String status,
			@RequestParam(defaultValue = "ALL") String target) {
		return ResponseEntity.ok(memberService.selectAdminList(status, target));
	}

	@Operation(summary="본사관리자 목록 조회", description="ADMIN 테이블에서 본사관리자 목록만 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/company/member/admin-list")
	public ResponseEntity<List<AdminListResponse>> selectCompanyAdminList(
			@RequestParam(defaultValue = "ALL") String status,
			HttpServletRequest request) {
		return ResponseEntity.ok(memberService.selectCompanyAdminList(status, getCompanyId(request)));
	}

	@Operation(summary="직원 목록 조회", description="EMPLOYEE 테이블에서 직원 목록만 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/company/member/employee-list")
	public ResponseEntity<List<Employee>> selectEmployeeList(
			@RequestParam(defaultValue = "ALL") String status,
			@RequestParam(defaultValue = "ALL") String isProgressed,
			HttpServletRequest request) {
		return ResponseEntity.ok(memberService.selectEmployeeList(status, isProgressed, getCompanyId(request)));
	}

	@Operation(summary="최고관리자용 관리자 계정 상세 조회", description="최고관리자가 ADMIN 계정 상세 정보를 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/super/member/admin/{adminId}")
	public ResponseEntity<?> selectSuperAdminDetail(@PathVariable Long adminId) {
		try {
			return ResponseEntity.ok(memberService.selectAdminDetail(adminId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@Operation(summary="본사관리자용 관리자 계정 상세 조회", description="본사관리자가 같은 회사의 본사관리자 계정 상세 정보를 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/company/member/admin/{adminId}")
	public ResponseEntity<?> selectCompanyAdminDetail(@PathVariable Long adminId, HttpServletRequest request) {
		try {
			AdminDetailResponse response = memberService.selectCompanyAdminDetail(adminId, getCompanyId(request));
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}

	@Operation(summary="본사관리자용 직원 계정 상세 조회", description="본사관리자가 같은 회사의 직원 계정 상세 정보를 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/company/member/employee/{employeeId}")
	public ResponseEntity<?> selectEmployeeDetail(@PathVariable Long employeeId, HttpServletRequest request) {
		try {
			EmployeeDetailResponse response = memberService.selectEmployeeDetail(employeeId, getCompanyId(request));
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}

	@Operation(summary="직원 본인 정보 조회", description="로그인한 직원 본인의 계정 정보만 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/employee/my-info")
	public ResponseEntity<?> selectMyEmployeeInfo(HttpServletRequest request) {
		try {
			EmployeeDetailResponse response = memberService.selectEmployeeSelf(getLoginId(request), getCompanyId(request));
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}

	@Operation(summary="최고관리자용 관리자 계정 수정", description="최고관리자가 ADMIN 계정 정보를 수정합니다.")
	@ApiResponse(responseCode="200", description="수정 성공")
	@PutMapping("/admin/super/member/admin/{adminId}")
	public ResponseEntity<?> updateSuperAdmin(@PathVariable Long adminId, @RequestBody AdminUpdateRequest request) {
		try {
			return ResponseEntity.ok(memberService.updateAdmin(adminId, request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="본사관리자용 관리자 계정 수정", description="본사관리자가 같은 회사의 본사관리자 계정을 수정합니다.")
	@ApiResponse(responseCode="200", description="수정 성공")
	@PutMapping("/admin/company/member/admin/{adminId}")
	public ResponseEntity<?> updateCompanyAdmin(
			@PathVariable Long adminId,
			@RequestBody AdminUpdateRequest request,
			HttpServletRequest httpRequest) {
		try {
			return ResponseEntity.ok(memberService.updateCompanyAdmin(adminId, getCompanyId(httpRequest), request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="본사관리자용 직원 계정 수정", description="본사관리자가 같은 회사의 직원 계정을 수정합니다.")
	@ApiResponse(responseCode="200", description="수정 성공")
	@PutMapping("/admin/company/member/employee/{employeeId}")
	public ResponseEntity<?> updateEmployee(
			@PathVariable Long employeeId,
			@RequestBody EmployeeUpdateRequest request,
			HttpServletRequest httpRequest) {
		try {
			return ResponseEntity.ok(memberService.updateEmployee(employeeId, getCompanyId(httpRequest), request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="본사관리자용 직원 계정 승인", description="승인 대기 상태의 같은 회사 직원 계정을 활성화하고 승인 안내 메일을 전송합니다.")
	@ApiResponse(responseCode="200", description="승인 성공")
	@PutMapping("/admin/company/member/employee/{employeeId}/approval")
	public ResponseEntity<?> approveEmployee(
			@PathVariable Long employeeId,
			HttpServletRequest httpRequest) {
		try {
			return ResponseEntity.ok(memberService.approveEmployee(employeeId, getCompanyId(httpRequest)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="직원 본인 정보 수정", description="로그인한 직원 본인의 정보만 수정합니다.")
	@ApiResponse(responseCode="200", description="수정 성공")
	@PutMapping("/employee/my-info")
	public ResponseEntity<?> updateMyEmployeeInfo(
			@RequestBody EmployeeUpdateRequest request,
			HttpServletRequest httpRequest) {
		try {
			return ResponseEntity.ok(memberService.updateEmployeeSelf(getLoginId(httpRequest), getCompanyId(httpRequest), request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="회사 정보 확인", description="사업자번호와 회사명이 일치하는 회사가 있는지 확인합니다.")
	@ApiResponse(responseCode="200", description="확인 성공")
	@GetMapping("/public/company/check")
	public ResponseEntity<Boolean> checkCompany(
			@RequestParam String businessNo,
			@RequestParam String companyName) {
		return ResponseEntity.ok(memberService.existsCompany(businessNo, companyName));
	}

	@Operation(summary="직원 로그인 아이디 중복 확인", description="EMPLOYEE 테이블 내 로그인 아이디 중복 여부를 확인합니다.")
	@ApiResponse(responseCode="200", description="확인 성공")
	@GetMapping("/public/employee/check-login-id")
	public ResponseEntity<Boolean> checkEmployeeLoginId(@RequestParam String loginId) {
		return ResponseEntity.ok(!memberService.existsEmployeeLoginId(loginId));
	}

	@Operation(summary="직원 회원가입 신청", description="회사 정보와 아이디/비밀번호 검증 후 회원가입 신청 상태의 직원을 생성합니다.")
	@ApiResponse(responseCode="200", description="신청 성공")
	@PostMapping("/public/employee/signup")
	public ResponseEntity<?> signupEmployee(@RequestBody EmployeeSignupRequest request) {
		try {
			return ResponseEntity.ok(memberService.signupEmployee(request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="직원 아이디 찾기 인증번호 발송", description="직원 본인 확인 정보가 일치하면 등록된 이메일로 인증번호를 발송합니다.")
	@ApiResponse(responseCode="200", description="인증번호 발송 성공")
	@PostMapping("/public/employee/recovery/login-id/request")
	public ResponseEntity<?> requestEmployeeLoginId(@RequestBody EmployeeFindIdRequest request) {
		try {
			return ResponseEntity.ok(Map.of("requestId", employeeAccountRecoveryService.requestLoginId(request)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="직원 비밀번호 찾기 인증번호 발송", description="직원 아이디와 일치하는 계정의 이메일로 인증번호를 발송합니다.")
	@ApiResponse(responseCode="200", description="인증번호 발송 성공")
	@PostMapping("/public/employee/recovery/password/request")
	public ResponseEntity<?> requestEmployeePasswordReset(@RequestBody EmployeeFindPasswordRequest request) {
		try {
			return ResponseEntity.ok(Map.of("requestId", employeeAccountRecoveryService.requestPasswordReset(request)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="직원 계정 찾기 인증번호 확인", description="이메일로 받은 일회성 인증번호를 확인합니다.")
	@ApiResponse(responseCode="200", description="인증 성공")
	@PostMapping("/public/employee/recovery/verify")
	public ResponseEntity<?> verifyEmployeeRecovery(@RequestBody EmployeeRecoveryVerifyRequest request) {
		try {
			EmployeeAccountRecoveryService.RecoveryVerification verification =
					employeeAccountRecoveryService.verifyCode(request);
			return ResponseEntity.ok(Map.of(
					"loginId", verification.loginId() == null ? "" : verification.loginId(),
					"resetToken", verification.resetToken() == null ? "" : verification.resetToken()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@Operation(summary="직원 비밀번호 재설정", description="인증이 완료된 직원 계정의 비밀번호를 변경합니다.")
	@ApiResponse(responseCode="200", description="비밀번호 변경 성공")
	@PostMapping("/public/employee/recovery/password/reset")
	public ResponseEntity<?> resetEmployeePassword(@RequestBody EmployeePasswordResetRequest request) {
		try {
			employeeAccountRecoveryService.resetPassword(request);
			return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private Long getCompanyId(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		String token = authorization.substring(7);
		return authService.getCompanyId(token);
	}

	private String getLoginId(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		String token = authorization.substring(7);
		return authService.getLoginId(token);
	}
}
