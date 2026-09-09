package com.kh.workation.reservation.model.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.vo.Reservation;
import com.kh.workation.reservation.model.vo.ReservationDate;

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
        
        // 1. 상태 변경 및 취소 정보 저장 
        progress.setStatus("CANCELLED");
        
        reservation.setStatus("CANCELLED");
        reservation.setCancelledReason(reason);       // 취소 사유 저장
        reservation.setCancelledDate(LocalDate.now()); // 취소 일자 저장 (현재 날짜)
        
        // 2. 차감되었던 객실 수 다시 복구
        Facility facility = app.getFacility();
        if (facility != null) {
            facility.increaseRoomCount();
        }
        
        // 3. 예약 날짜를 기반으로 복구할 실제 워케이션 일수 계산
        ReservationDate resDate = app.getReservationDate();
        int restoreDays = 0;

        if (resDate != null && resDate.getStartDate() != null && resDate.getEndDate() != null) {
            restoreDays = (int) ChronoUnit.DAYS.between(resDate.getStartDate(), resDate.getEndDate()) + 1;
        }
        
        // 4. 크루원 일수 복구 처리
        if (app.getCrew() != null && restoreDays > 0) {
            // 크루 정보 조회
            Crew crew = crewDao.findById(app.getCrew().getCrewId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

            // 해당 크루의 현재 활동 중인(ACTIVE) 크루원 및 Employee 조회 (Fetch Join)
            List<CrewMemberHist> activeMembers = 
                    crewMemberHistDao.findByCrewCrewIdAndStatusWithEmployee(crew.getCrewId(), "ACTIVE");

            // 각 크루원들의 잔여 워케이션 일수(workationAvailDays)에 실제 예약 일수(+restoreDays) 복구
            for (CrewMemberHist memberHist : activeMembers) {
                Employee employee = memberHist.getEmployee();

                if (employee != null) {
                    int currentDays = employee.getWorkationAvailDays() != null 
                            ? employee.getWorkationAvailDays() 
                            : 0;

                    // 실제 예약했던 일수만큼 복구
                    employee.setWorkationAvailDays(currentDays + restoreDays);

                    // 변경 사항 저장
                    employeeDao.save(employee);
                }
            }
        }
        
        return reservation;
    }

}
