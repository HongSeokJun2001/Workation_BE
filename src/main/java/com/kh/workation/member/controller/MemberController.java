package com.kh.workation.member.controller;
	
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.member.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Member API", description="관리자 및 직원 관련 API")

@CrossOrigin
@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Operation(summary="최고관리자 및 본사관리자 목록 조회", description="ADMIN 테이블에서 최고관리자와 본사관리자를 함께 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/super-admin/list")
	public ResponseEntity<List<Admin>> selectAdminList(
			@RequestParam(defaultValue = "ALL") String status,
			@RequestParam(defaultValue = "ALL") String target) {
		return ResponseEntity.ok(memberService.selectAdminList(status, target));
	}

	@Operation(summary="본사관리자 목록 조회", description="ADMIN 테이블에서 본사관리자 목록만 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/company-admin/list")
	public ResponseEntity<List<Admin>> selectCompanyAdminList(
			@RequestParam(defaultValue = "ALL") String status) {
		return ResponseEntity.ok(memberService.selectCompanyAdminList(status));
	}

	@Operation(summary="직원 목록 조회", description="EMPLOYEE 테이블에서 직원 목록만 조회합니다.")
	@ApiResponse(responseCode="200", description="조회 성공")
	@GetMapping("/admin/employee/list")
	public ResponseEntity<List<Employee>> selectEmployeeList(
			@RequestParam(defaultValue = "ALL") String status) {
		return ResponseEntity.ok(memberService.selectEmployeeList(status));
	}
}
