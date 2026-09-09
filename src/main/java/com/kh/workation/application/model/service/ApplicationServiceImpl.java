package com.kh.workation.application.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.dao.ApprovalDao;
import com.kh.workation.application.model.dao.ProgressDao;
import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.dto.ApplicationList;
import com.kh.workation.application.model.dto.ApplicationSearch;
import com.kh.workation.application.model.specification.ApplicationSpecification;
import com.kh.workation.application.model.vo.Application;
import com.kh.workation.application.model.vo.Approval;
import com.kh.workation.application.model.vo.Progress;
import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.crew.model.dao.CrewMemberHistDao;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.dao.ReservationDateDao;
import com.kh.workation.reservation.model.vo.Reservation;
import com.kh.workation.reservation.model.vo.ReservationDate;

@Service
public class ApplicationServiceImpl implements ApplicationService{
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
    private ReservationDateDao reservationDateDao;
    
    @Autowired
    private ApprovalDao approvalDao;
    
    @Autowired
    private ProgressDao progressDao;
    
    @Autowired
    private ReservationDao reservationDao;
    
    @Autowired 
    private CrewDao crewDao;
    
    @Autowired
    private CrewMemberHistDao crewMemberHistDao;
    
    @Autowired
    private EmployeeDao employeeDao;
	
    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationList> getApplicationList(Pageable pageable, Long companyId, ApplicationSearch searchDto) {
        
        // Specification(동적 조건) 조립
        Specification<Application> spec = ApplicationSpecification.searchWith(searchDto, companyId);
        
        // 조립된 조건으로 DB 조회
        Page<Application> page = applicationDao.findAll(spec, pageable);
        
        // Application -> ApplicationList DTO 변환 후 반환
        return page.map(ApplicationList::new);
    }
	
	@Override
	@Transactional(readOnly = true)
	public Page<ApplicationList> getApplicationMemberList(Pageable pageable, String loginId, ApplicationSearch searchDto) {
		
		Specification<Application> spec = ApplicationSpecification.searchWith(searchDto, loginId);
		
		Page<Application> page = applicationDao.findAll(spec, pageable);
		
		return page.map(ApplicationList::new);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ApplicationDetail getApplicationDetail(int workationId, Long loginCompanyId) {
	    Application application = applicationDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 신청 내역을 찾을 수 없습니다. id=" + workationId));

	    // companyId가 존재하는 기업 관리자인 경우, 해당 기업의 신청 내역인지 검증 (null이면 전체 관리자이므로 통과)
	    if (loginCompanyId != null) {
	        Long appCompanyId = null;

	        if (application.getCrew() != null) {
	            // 1. Crew에 Company가 정상 매핑되어 있는 경우
	            if (application.getCrew().getCompany() != null) {
	                appCompanyId = application.getCrew().getCompany().getCompanyId();
	            } 
	            // 2. [임시 대응] Crew의 Company가 null인 경우 크루장(Employee)의 companyId 참조
	            else if (application.getCrew().getEmployee() != null) {
	                appCompanyId = application.getCrew().getEmployee().getCompanyId();
	            }
	        }

	        // 검증: 기업 ID를 찾을 수 없거나 로그인한 기업 관리자의 ID와 불일치할 경우 예외 처리
	        if (appCompanyId == null || !loginCompanyId.equals(appCompanyId)) {
	            throw new AccessDeniedException("소속 기업의 워케이션 신청 내역만 조회할 수 있습니다.");
	        }
	    }

	    return new ApplicationDetail(application);
	}

	@Override
	@Transactional(readOnly = true)
	public ApplicationDetail getApplicationMemberDetail(int workationId, String loginId) {
	    Application application = applicationDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 신청 내역을 찾을 수 없습니다. id=" + workationId));

	    // 해당 신청의 크루원(리더 포함) 목록 중 로그인한 사원(loginId)이 존재하는지 검증
	    boolean isCrewMember = application.getCrew().getCrewMemberHists().stream()
	            .anyMatch(hist -> hist.getEmployee().getLoginId().equals(loginId)
	                           && "ACTIVE".equalsIgnoreCase(hist.getStatus())); // 탈퇴한 멤버 제외 필요 시 조건 유지

	    if (!isCrewMember) {
	        throw new AccessDeniedException("본인이 속한 크루의 워케이션 예약 내역만 조회할 수 있습니다.");
	    }
	    
	    // 로그인한 유저 ID와 신청건의 leaderId 비교
	    boolean isLeader = application.getCrew().getEmployee().getLoginId().equals(loginId);

	    // 3. DTO 생성 후 isLeader 설정
	    ApplicationDetail detail = new ApplicationDetail(application);
	    detail.setIsleader(isLeader); 

	    return detail;
	}
	
	@Override
	@Transactional
    public Application insertApplication(Application a) {
		
		// 1. 크루 정보 조회
	    Crew crew = crewDao.findById(a.getCrew().getCrewId())
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

	    // 2. 크루에 설정된 워케이션 사용 일수(workUsedDays) 가져오기
	    Integer usedDays = crew.getWorkUsedDays();
	    if (usedDays == null || usedDays <= 0) {
	        usedDays = 1; // 기본값 방어 로직
	    }

	    // 3. 해당 크루의 현재 활동 중인(ACTIVE) 크루원 목록 조회
	    List<CrewMemberHist> activeMembers = crewMemberHistDao.findByCrewCrewIdAndStatusWithEmployee(crew.getCrewId(), "ACTIVE");
	    
	    for (CrewMemberHist memberHist : activeMembers) {
	        Employee employee = memberHist.getEmployee();
	        if (employee != null) {
	            int currentDays = employee.getWorkationAvailDays() != null ? employee.getWorkationAvailDays() : 0;
	            
	            // 보유 일수가 차감할 일수보다 적은 크루원이 있는 경우
	            if (currentDays < usedDays) {
	                throw new IllegalArgumentException(
	                    String.format("크루원 '%s'님의 잔여 워케이션 일수(%d일)가 필요 일수(%d일)보다 부족하여 신청할 수 없습니다.", 
	                        employee.getEmployeeName(), currentDays, usedDays)
	                );
	            }
	        }
	    }

	    // 4. 크루원들의 잔여 워케이션 일수(workationAvailDays) 차감
	    for (CrewMemberHist memberHist : activeMembers) {
	        Employee employee = memberHist.getEmployee();
	        
	        if (employee != null) {
	            int currentDays = employee.getWorkationAvailDays() != null ? employee.getWorkationAvailDays() : 0;
	            
	            // 크루의 workUsedDays만큼 일수 차감 (음수 방지)
	            employee.setWorkationAvailDays(Math.max(0, currentDays - usedDays));
	            
	            // 변경 사항 저장
	            employeeDao.save(employee); 
	        }
	    }

        // ReservationDate(예약날짜)
        if (a.getReservationDate() != null) {
            ReservationDate savedDate = reservationDateDao.save(a.getReservationDate());
            a.setReservationDate(savedDate);
        }

        // Application(신청 정보)
        Application savedApp = applicationDao.save(a);

        // Progress(진행상태)
        Progress progress = new Progress();
        progress.setWorkationId(savedApp.getWorkationId()); 
        progress.setStatus("APPLY");                        
        progressDao.save(progress);

        return savedApp;
    }
	
	@Override
	@Transactional
	public Application approveApplication(int workationId, Long adminId) {
		
		Application app = applicationDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
		
		Progress progress = progressDao.findById(workationId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
		progress.setStatus("CONFIRM"); 
		
		// 연관된 Facility 잔여 객실 수 -1 차감
		Facility facility = app.getFacility();
		if(facility != null) {
			// Facility VO의 decreaseRoomCount() 호출
			// 잔여 객실이 0개 이하면 IllegalStateException("남은 객실이 없습니다.") 예외 발생
			facility.decreaseRoomCount();
		} else {
			throw new IllegalArgumentException("신청 정보에 연관된 시설 정보가 없습니다.");
		}
        
        Reservation reservation = new Reservation();
        reservation.setApplication(app);
        reservation.setFacility(app.getFacility());
        reservation.setReservationDate(app.getReservationDate());
        reservation.setStatus("RESERVED");

        reservationDao.save(reservation);
        
        Approval approval = new Approval();
        approval.setWorkationId(workationId);
        approval.setAdminId(adminId);
        approval.setApprovedYn("APPROVED");        
        approvalDao.save(approval);
		
        return app;
	}
	
	@Override
	@Transactional
	public Application cancelApplication(int workationId, Long adminId, String reason) {
	    
	    Application app = applicationDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
	    
	    Progress progress = progressDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
	    
	    if ("CANCELLED".equals(progress.getStatus())) {
	        throw new IllegalStateException("이미 취소 처리된 신청 건입니다.");
	    }
	    
	    // 승인 상태에서 취소하는 경우, 차감했던 시설 객실 수 복구
	    if("CONFIRM".equals(progress.getStatus())) {
	    	Facility facility = app.getFacility();
	    	if(facility != null) {
	    		facility.increaseRoomCount(); // Facility 엔터티에 객실 수 + 1 로직 호출
	    	}
	    }
	    
	    progress.setStatus("CANCELLED");
	    
	    Approval approval = new Approval();
	    approval.setWorkationId(workationId);
	    approval.setAdminId(adminId);
	    approval.setApprovedYn("REJECT");
	    approval.setRejectReason(reason);
	    approvalDao.save(approval);
	    
	    if (app.getCrew() != null) {
	        // 크루 정보 조회
	        Crew crew = crewDao.findById(app.getCrew().getCrewId())
	                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

	        // 크루에 설정되었던 워케이션 사용 일수 가져오기
	        Integer usedDays = crew.getWorkUsedDays();
	        if (usedDays == null || usedDays <= 0) {
	            usedDays = 1; // 기본값 방어
	        }

	        // 해당 크루의 현재 활동 중인(ACTIVE) 크루원 및 Employee 조회 (Fetch Join 적용 권장)
	        List<CrewMemberHist> activeMembers = crewMemberHistDao.findByCrewCrewIdAndStatusWithEmployee(crew.getCrewId(), "ACTIVE");

	        // 크루원들의 잔여 워케이션 일수(workationAvailDays) 복구 (+usedDays)
	        for (CrewMemberHist memberHist : activeMembers) {
	            Employee employee = memberHist.getEmployee();

	            if (employee != null) {
	                int currentDays = employee.getWorkationAvailDays() != null ? employee.getWorkationAvailDays() : 0;

	                // 차감했던 일수 복구
	                employee.setWorkationAvailDays(currentDays + usedDays);

	                // 변경 사항 저장
	                employeeDao.save(employee);
	            }
	        }
	    }
	    
	    return app;
	}
	
	@Override
    @Transactional(readOnly = false)
    public int updateFinishedWorkationStatus() {
        LocalDate today = LocalDate.now();

     // 1. 오늘 이전 날짜로 끝난 진행 중 신청건 목록 조회
        List<Progress> expiredProgressList = progressDao.findExpiredProgressList(today);

        int count = 0;
        for (Progress progress : expiredProgressList) {
            // Progress 상태 변경
            progress.setStatus("COMPLETED");

            // 연관된 Application 및 Facility 객실 수 복구
            applicationDao.findById(progress.getWorkationId()).ifPresent(app -> {
            	Facility facility = app.getFacility();
            	if(facility != null) {
            		facility.increaseRoomCount(); // 사용 종류 시 객실 수 + 1
            	}
            });
            
            // 연관된 Reservation 상태 변경
            reservationDao.findByApplication_WorkationId(progress.getWorkationId())
                .ifPresent(reservation -> {
                    reservation.setStatus("COMPLETED");
                });

            count++;
        }

        return count;
    }
	
	
}
