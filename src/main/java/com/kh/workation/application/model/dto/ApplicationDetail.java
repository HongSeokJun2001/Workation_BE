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
    
    // Entity(Application) -> DTO 변환 생성자
    public ApplicationDetail(Application a) {
        this.workationId = a.getWorkationId();
        this.purpose = a.getPurpose();

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
            this.region = a.getFacility().getRegion();
        }
    }


}
