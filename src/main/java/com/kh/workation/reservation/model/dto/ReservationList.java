package com.kh.workation.reservation.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kh.workation.reservation.model.vo.Reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationList {
	
	private Integer reservationId;
    private String leaderName;     
    private LocalDate startDate;   
    private LocalDate endDate;     
    private String facilityName;   
    private String status;         
    private LocalDateTime createdDate;
    
    public ReservationList(Reservation r) {
    	this.reservationId = r.getReservationId();
        this.createdDate = r.getCreatedDate();
        this.status = r.getStatus();

        // 크루장
        if (r.getApplication() != null && r.getApplication().getCrew() != null && r.getApplication().getCrew().getEmployee() != null) {
            this.leaderName = r.getApplication().getCrew().getEmployee().getEmployeeName(); 
        }

        // 신청기간
        if (r.getReservationDate() != null) {
            this.startDate = r.getReservationDate().getStartDate();
            this.endDate = r.getReservationDate().getEndDate();
        }

        // 시설명
        if (r.getFacility() != null) {
            this.facilityName = r.getFacility().getFacilityName();
        }
    }

}
