package com.kh.workation.common.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.facility.model.service.FacilityService;
import com.kh.workation.member.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Platform Stats API", description = "로그인 화면 노출용 통계 API")
@RestController
@CrossOrigin
public class PlatformStatsController {

	private final MemberService memberService;
	private final FacilityService facilityService;

	public PlatformStatsController(MemberService memberService, FacilityService facilityService) {
		this.memberService = memberService;
		this.facilityService = facilityService;
	}

	@Operation(summary = "플랫폼 통계 조회", description = "활성 고객사 수와 활성 시설 수를 조회합니다.")
	@GetMapping("/public/platform/stats")
	public ResponseEntity<Map<String, Long>> selectPlatformStats() {
		return ResponseEntity.ok(Map.of(
				"companyCount", memberService.countActiveCompanies(),
				"facilityCount", facilityService.countActiveFacilities()));
	}
}
