package com.kh.workation.reservation.model.vo;

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

@Entity
@Table(name="RESERVATION")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Reservation {

	@Id
	@Column(name = "RESERVATION_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int reservationId;
	
	@Column(name = "WORKATION_ID", nullable = false)
	private int workationId;
	
	@Column(name = "FACILITY_ID")
	private Integer facilityId; 
	
	@Column(name = "START_DATE", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "END_DATE", nullable = false)
	private LocalDate endDate;
	
	@Column(name="STATUS", columnDefinition="VARCHAR(20) DEFAULT 'RESERVED'")
	private String status;
	
	@Column(name="CREATED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdDate;
	
	@Column(name="CANCELLED_DATE")
	private LocalDate cancelledDate;
}
