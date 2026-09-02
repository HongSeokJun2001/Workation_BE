package com.kh.workation.reservation.model.dto;

import java.time.LocalDate;

import com.kh.workation.reservation.model.vo.Reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationDetail {
    
    private int reservationId;
    private int workationId;
    
    private String crewName;
    private String leaderName;

    private LocalDate startDate;
    private LocalDate endDate;

    private String facilityName;
    private String region;

    private String purpose;
    
    private String status;
    
    public ReservationDetail(Reservation r) {
        
        this.reservationId = r.getReservationId();
        this.status = r.getStatus();

        if (r.getApplication() != null) {
            this.workationId = r.getApplication().getWorkationId();
            this.purpose = r.getApplication().getPurpose();
            
            // 크루 및 크루장 이름 세팅
            if (r.getApplication().getCrew() != null) {
                this.crewName = r.getApplication().getCrew().getCrewName();
                if (r.getApplication().getCrew().getEmployee() != null) {
                    this.leaderName = r.getApplication().getCrew().getEmployee().getEmployeeName();
                }
            }
            
        }

        // 예약 날짜(시작일/종료일) 세팅
        if (r.getReservationDate() != null) {
            this.startDate = r.getReservationDate().getStartDate();
            this.endDate = r.getReservationDate().getEndDate();
        }

        // 시설 및 지역 세팅
        if (r.getFacility() != null) {
            this.facilityName = r.getFacility().getFacilityName();
            this.region = r.getFacility().getRegion();
        }
    }
}
