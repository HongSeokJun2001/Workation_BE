package com.kh.workation.application.model.vo;

import java.time.LocalDate;

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
@Table(name="WORKATION_PROGRESS")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Progress {

	@Id
	@Column(name = "PROG_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int progId;
	
	@Column(name = "WORKATION_ID", nullable = false)
	private int workationId;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'APPLY'")
	private String status;
	
	@Column(name="APPLY_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDate applyDate;
	
	@Column(name="CONFIRM_DATE")
	private LocalDate confirmDate;
	
	@Column(name="CANCELLED_DATE")
	private LocalDate cancelledDate;
	
}
