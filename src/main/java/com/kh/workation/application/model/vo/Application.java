package com.kh.workation.application.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Schema(description="워케이션 신청 엔티티")

@Entity
@Table(name="WORKATION_APPLICATION")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Application {

	@Id
	@Column(name = "WORKATION_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int workationId;
	
	@Column(name = "COMPANY_ID", nullable = false)
	private int companyId;
	
	@Column(name = "CREW_ID", nullable = false)
	private int crewId;
	
	@Column(name = "FACILITY_ID")
	private Integer facilityId; 
	
	@Column(name = "START_DATE", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "END_DATE", nullable = false)
	private LocalDate endDate;
	
	@Column(name = "REGION", length = 20)
	private String region;
	
	@Column(name = "PURPOSE", length = 900)
	private String purpose;
	
	@Column(name="CREATED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdDate;
	
}
