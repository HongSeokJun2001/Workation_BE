package com.kh.workation.reservation.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.dao.ProgressDao;
import com.kh.workation.application.model.vo.Application;
import com.kh.workation.application.model.vo.Progress;
import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.crew.model.dao.CrewMemberHistDao;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.dto.ReservationDetail;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.vo.Reservation;

@Service
public class ReservationServiceImpl implements ReservationService{
	
	@Autowired
    private ReservationDao reservationDao;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ProgressDao progressDao;
	
	@Autowired 
    private CrewDao crewDao;
    
    @Autowired
    private CrewMemberHistDao crewMemberHistDao;
    
    @Autowired
    private EmployeeDao employeeDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<ReservationList> getReservationList(Pageable pageable) {
	    
	    Page<Reservation> page = reservationDao.findAllByOrderByReservationIdDesc(pageable);
	    
	    return page.map(ReservationList::new);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ReservationDetail getReservationDetail(int reservationId) {
		
		Reservation reservation = reservationDao.findByReservationId(reservationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 예약 내역을 찾을 수 없습니다. id=" + reservationId));
		
		return new ReservationDetail(reservation);
	}
	
	@Override
	@Transactional
	public Reservation cancelReservation(int workationId, String reason) {
		
		Application app = applicationDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
	    
		// workationId로 예약 정보 조회
	    Reservation reservation = reservationDao.findByApplication_WorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 건입니다. workationId=" + workationId));
	    
	    // workationId로 Progress 조회
	    Progress progress = progressDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진행 상태 건입니다. workationId=" + workationId));
	    
	    if ("CANCELLED".equals(progress.getStatus()) || "CANCELLED".equals(reservation.getStatus())) {
	        throw new IllegalStateException("이미 취소 처리된 예약 건입니다.");
	    }
	    
	    // 상태 변경 및 취소 정보 저장 
	    progress.setStatus("CANCELLED");
	    
	    reservation.setStatus("CANCELLED");
	    reservation.setCancelledReason(reason);       // 취소 사유 저장
	    reservation.setCancelledDate(LocalDate.now()); // 취소 일자 저장 (현재 날짜)
	    
	    if (app.getCrew() != null) {
	        // 크루 정보 조회
	        Crew crew = crewDao.findById(app.getCrew().getCrewId())
	                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

	        // 크루에 설정되었던 워케이션 사용 일수 가져오기
	        Integer usedDays = crew.getWorkUsedDays();
	        if (usedDays == null || usedDays <= 0) {
	            usedDays = 1; // 기본값 방어
	        }

	        // 해당 크루의 현재 활동 중인(ACTIVE) 크루원 및 Employee 조회 (Fetch Join)
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
	    
	    return reservation;
	}

}
