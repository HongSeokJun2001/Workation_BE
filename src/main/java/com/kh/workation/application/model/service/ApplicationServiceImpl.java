package com.kh.workation.application.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public ApplicationDetail getApplicationDetail(int workationId) {
		
		Application application = applicationDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 신청 내역을 찾을 수 없습니다. id=" + workationId));
		
		return new ApplicationDetail(application);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ApplicationDetail getApplicationMemberDetail(int workationId) {
		
		Application application = applicationDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 신청 내역을 찾을 수 없습니다. id=" + workationId));
		
		return new ApplicationDetail(application);
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
