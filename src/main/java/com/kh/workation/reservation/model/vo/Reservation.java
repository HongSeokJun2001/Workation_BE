package com.kh.workation.reservation.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.application.model.vo.Application;
import com.kh.workation.facility.model.vo.Facility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKATION_ID")
    private Application application;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID")
    private Facility facility;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERV_DT_ID")
    private ReservationDate reservationDate;
	
	@Column(name="STATUS", columnDefinition="VARCHAR(20) DEFAULT 'RESERVED'")
	private String status;
	
	@Column(name="CREATED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdDate;
	
	@Column(name="CANCELLED_DATE")
	private LocalDate cancelledDate;
}
