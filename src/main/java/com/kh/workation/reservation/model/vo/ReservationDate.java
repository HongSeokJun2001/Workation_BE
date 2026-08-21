package com.kh.workation.reservation.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.application.model.vo.Approval;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.member.model.vo.Company;

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
@Table(name="RESERVATION_DATE")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class ReservationDate {

	@Id
	@Column(name = "RESERV_DT_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int reservDtId;
	
	@Column(name = "START_DATE", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "END_DATE", nullable = false)
	private LocalDate endDate;
}
