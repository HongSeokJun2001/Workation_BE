package com.kh.workation.common.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.crew.model.dao.CrewMemberHistDao;
import com.kh.workation.facility.model.service.FacilityService;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.service.MemberService;
import com.kh.workation.reservation.model.dao.ReservationDao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Platform Stats API", description = "로그인 화면 노출용 통계 API")
@RestController
@CrossOrigin
public class PlatformStatsController {

	private final MemberService memberService;
	private final FacilityService facilityService;
	private final EmployeeDao employeeDao;
	private final ReservationDao reservationDao;
	private final ApplicationDao applicationDao;
	private final CrewMemberHistDao crewMemberHistDao;
	private final AuthService authService;

	public PlatformStatsController(
			MemberService memberService,
			FacilityService facilityService,
			EmployeeDao employeeDao,
			ReservationDao reservationDao,
			ApplicationDao applicationDao,
			CrewMemberHistDao crewMemberHistDao,
			AuthService authService) {
		this.memberService = memberService;
		this.facilityService = facilityService;
		this.employeeDao = employeeDao;
		this.reservationDao = reservationDao;
		this.applicationDao = applicationDao;
		this.crewMemberHistDao = crewMemberHistDao;
		this.authService = authService;
	}

	@Operation(summary = "플랫폼 통계 조회", description = "활성 고객사 수와 활성 시설 수를 조회합니다.")
	@GetMapping("/public/platform/stats")
	public ResponseEntity<Map<String, Long>> selectPlatformStats() {
		return ResponseEntity.ok(Map.of(
				"companyCount", memberService.countActiveCompanies(),
				"facilityCount", facilityService.countActiveFacilities()));
	}

	@Operation(summary = "최고관리자 대시보드 통계 조회", description = "최고관리자 홈 화면에 표시할 전체 통계를 조회합니다.")
	@GetMapping("/admin/super/dashboard/stats")
	public ResponseEntity<Map<String, Long>> selectSuperDashboardStats() {
		return ResponseEntity.ok(Map.of(
				"facilityCount", facilityService.countActiveFacilities(),
				"companyCount", memberService.countActiveCompanies(),
				"employeeCount", employeeDao.count(),
				"reservationCount", reservationDao.count()));
	}

	@Operation(summary = "본사관리자 대시보드 통계 조회", description = "본사관리자 홈 화면에 표시할 회사 통계를 조회합니다.")
	@GetMapping("/admin/company/dashboard/stats")
	public ResponseEntity<Map<String, Long>> selectCompanyDashboardStats(HttpServletRequest request) {
		Long companyId = authService.getCompanyId(getToken(request));
		LocalDate today = LocalDate.now();
		LocalDate monthStart = today.withDayOfMonth(1);

		return ResponseEntity.ok(Map.of(
				"employeeCount", employeeDao.countByCompanyId(companyId),
				"pendingEmployeeCount", employeeDao.countByCompanyIdAndIsProgressed(companyId, "N"),
				"pendingApplicationCount", applicationDao.countPendingApplicationsByCompany(companyId),
				"approvedThisMonthCount", applicationDao.countConfirmedApplicationsByCompanyAndConfirmDateBetween(companyId, monthStart, today)));
	}

	@Operation(summary = "직원 대시보드 통계 조회", description = "직원 홈 화면에 표시할 개인 통계를 조회합니다.")
	@GetMapping("/employee/dashboard/stats")
	public ResponseEntity<Map<String, Long>> selectEmployeeDashboardStats(HttpServletRequest request) {
		String loginId = authService.getLoginId(getToken(request));

		return ResponseEntity.ok(Map.of(
				"joinedCrewCount", crewMemberHistDao.countActiveCrewMemberships(loginId),
				"pendingApplicationCount", applicationDao.countPendingApplicationsByCrewMember(loginId),
				"approvedReservationCount", applicationDao.countConfirmedApplicationsByCrewMember(loginId),
				"reviewableFacilityCount", reservationDao.countReviewableFacilities(loginId)));
	}

	private String getToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return authorization == null ? "" : authorization.substring(7);
	}
}
