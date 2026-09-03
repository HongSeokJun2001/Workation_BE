package com.kh.workation.application.model.dto;

import java.time.LocalDate;

import com.kh.workation.application.model.vo.Application;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDetail {
	
	private int workationId;

    private String crewName;
    private String leaderName;

    private LocalDate startDate;
    private LocalDate endDate;

    private String facilityName;
    private String region;

    private String purpose;
    
    private String status;
    
    private String rejectReason;
    private String approvedYn;
    
    private String cancelledReason;
    
    // Entity(Application) -> DTO 변환 생성자
    public ApplicationDetail(Application a) {
        this.workationId = a.getWorkationId();
        this.purpose = a.getPurpose();
        this.region = a.getRegion();

        // 크루 및 크루장 이름 세팅 (Null 방어)
        if (a.getCrew() != null) {
            this.crewName = a.getCrew().getCrewName();
            if (a.getCrew().getEmployee() != null) {
                this.leaderName = a.getCrew().getEmployee().getEmployeeName();
            }
        }

        // 예약 날짜(시작일/종료일) 세팅
        if (a.getReservationDate() != null) {
            this.startDate = a.getReservationDate().getStartDate();
            this.endDate = a.getReservationDate().getEndDate();
        }

        // 시설 및 지역 세팅
        if (a.getFacility() != null) {
            this.facilityName = a.getFacility().getFacilityName();
        }
        
        // 예약 상태 세팅
        if (a.getProgress() != null) {
        	this.status = a.getProgress().getStatus();
        }
        
        // 예약 승인 세팅
        if (a.getApproval() != null) {
        	this.rejectReason = a.getApproval().getRejectReason();
        	this.approvedYn = a.getApproval().getApprovedYn();
        }
        
        if (a.getReservation() != null) {
        	this.cancelledReason = a.getReservation().getCancelledReason();
        }
    }


}
