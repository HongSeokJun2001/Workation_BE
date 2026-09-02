package com.kh.workation.application.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kh.workation.application.model.vo.Application;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationList {
	
	private Integer workationId;
    private String leaderName;     
    private LocalDate startDate;   
    private LocalDate endDate;     
    private String facilityName;   
    private String status;         
    private LocalDateTime createdDate; 
    private String region;

    public ApplicationList(Application a) {
    	this.workationId = a.getWorkationId();
        this.createdDate = a.getCreatedDate();
        this.status = a.getStatus();
        this.region = a.getRegion();

        // 크루장
        if (a.getCrew() != null && a.getCrew().getEmployee() != null) {
            this.leaderName = a.getCrew().getEmployee().getEmployeeName(); 
        }

        // 신청기간
        if (a.getReservationDate() != null) {
            this.startDate = a.getReservationDate().getStartDate();
            this.endDate = a.getReservationDate().getEndDate();
        }

        // 시설명
        if (a.getFacility() != null) {
            this.facilityName = a.getFacility().getFacilityName();
        }
    }

}
