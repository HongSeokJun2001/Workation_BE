package com.kh.workation.crew.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kh.workation.crew.model.vo.Crew;

import lombok.Getter;

@Getter
public class CrewResponse {
	
	private Integer crewId;
    private String crewName;
    private String crewContent;
    private String status;
    private LocalDate createDate;
    private LocalDate endDate;
    private Integer capacity;
    private Integer workUsedDays;
    
    private Long companyId;
    private Long leaderEmployeeId;

    public CrewResponse(Crew crew) {
        this.crewId = crew.getCrewId();
        this.crewName = crew.getCrewName();
        this.crewContent = crew.getCrewContent();
        this.status = crew.getStatus();
        this.createDate = crew.getCreatedDate();
        this.endDate = crew.getEndDate();
        this.capacity = crew.getCapacity();
        this.workUsedDays = crew.getWorkUsedDays();

        if (crew.getCompany() != null) {
            this.companyId = crew.getCompany().getCompanyId();
        }
        if (crew.getEmployee() != null) {
            this.leaderEmployeeId = crew.getEmployee().getEmployeeId(); 
        }
    }

}
