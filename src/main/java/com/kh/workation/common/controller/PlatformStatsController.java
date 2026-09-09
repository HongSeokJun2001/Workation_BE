package com.kh.workation.common.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.dto.ApplicationList;
import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.facility.model.dao.FacilityDao;
import com.kh.workation.facility.model.service.FacilityService;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.service.MemberService;
import com.kh.workation.notice.model.dao.NoticeDao;
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
	private final FacilityDao facilityDao;
	private final EmployeeDao employeeDao;
	private final ReservationDao reservationDao;
	private final ApplicationDao applicationDao;
	private final CrewDao crewDao;
	private final NoticeDao noticeDao;
	private final AuthService authService;

	public PlatformStatsController(
			MemberService memberService,
			FacilityService facilityService,
			FacilityDao facilityDao,
			EmployeeDao employeeDao,
			ReservationDao reservationDao,
			ApplicationDao applicationDao,
			CrewDao crewDao,
			NoticeDao noticeDao,
			AuthService authService) {
		this.memberService = memberService;
		this.facilityService = facilityService;
		this.facilityDao = facilityDao;
		this.employeeDao = employeeDao;
		this.reservationDao = reservationDao;
		this.applicationDao = applicationDao;
		this.crewDao = crewDao;
		this.noticeDao = noticeDao;
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
	public ResponseEntity<Map<String, Object>> selectSuperDashboardStats() {
		List<Map<String, Object>> recentNotices = noticeDao
				.findByStatusOrderByNoticeIdDesc("Y", PageRequest.of(0, 5))
				.getContent().stream()
				.map(notice -> Map.<String, Object>of(
						"noticeId", notice.getNoticeId(),
						"title", notice.getNoticeTitle()))
				.toList();

		return ResponseEntity.ok(Map.of(
				"activeFacilityCount", facilityDao.countByStatus("ACTIVE"),
				"inactiveFacilityCount", facilityDao.countByStatus("INACTIVE"),
				"companyCount", memberService.countActiveCompanies(),
				"approvedReservationCount", reservationDao.countByStatus("RESERVED"),
				"recentNotices", recentNotices));
	}

	@Operation(summary = "본사관리자 대시보드 통계 조회", description = "본사관리자 홈 화면에 표시할 회사 통계를 조회합니다.")
	@GetMapping("/admin/company/dashboard/stats")
	public ResponseEntity<Map<String, Object>> selectCompanyDashboardStats(HttpServletRequest request) {
		Long companyId = authService.getCompanyId(getToken(request));
		List<ApplicationList> pendingApplications = applicationDao
				.findByCompanyCompanyIdAndProgressStatusOrderByWorkationIdDesc(
						companyId, "APPLY", PageRequest.of(0, 5))
				.stream()
				.map(ApplicationList::new)
				.toList();

		return ResponseEntity.ok(Map.of(
				"employeeCount", employeeDao.countByCompanyIdAndIsProgressed(companyId, "Y"),
				"pendingEmployeeCount", employeeDao.countByCompanyIdAndIsProgressed(companyId, "N"),
				"pendingApplicationCount", applicationDao.countPendingApplicationsByCompany(companyId),
				"pendingApplications", pendingApplications));
	}

	@Operation(summary = "직원 대시보드 통계 조회", description = "직원 홈 화면에 표시할 개인 통계를 조회합니다.")
	@GetMapping("/employee/dashboard/stats")
	public ResponseEntity<Map<String, Object>> selectEmployeeDashboardStats(HttpServletRequest request) {
		String loginId = authService.getLoginId(getToken(request));
		String employeeName = employeeDao.findByLoginIdAndStatus(loginId, "ACTIVE")
				.map(employee -> employee.getEmployeeName())
				.orElse(null);
		List<Map<String, Object>> reviewableFacilities = reservationDao.findReviewableFacilities(loginId).stream()
				.map(facility -> Map.<String, Object>of(
						"facilityId", facility.getFacilityId(),
						"facilityName", facility.getFacilityName(),
						"region", facility.getRegion()))
				.toList();

		return ResponseEntity.ok(Map.of(
				"employeeName", employeeName == null ? "" : employeeName,
				"joinedCrewCount", crewDao.countDistinctParticipatingCrews(loginId),
				"pendingApplicationCount", applicationDao.countPendingApplicationsByCrewMember(loginId),
				"approvedReservationCount", applicationDao.countConfirmedApplicationsByCrewMember(loginId),
				"reviewableFacilities", reviewableFacilities));
	}

	private String getToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return authorization == null ? "" : authorization.substring(7);
	}
}
