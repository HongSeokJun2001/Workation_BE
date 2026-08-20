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
@Table(name="WORKATION_APPROVAL")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Approval {
	
	@Id
	@Column(name = "APPROVE_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int approveId;
	
	@Column(name = "WORKATION_ID", nullable = false)
	private int workationId;

	@Column(name = "ADMIN_ID", nullable = false)
	private int adminId;
	
	@Column(name = "APPROVED_YN", length = 20, nullable = false)
	private String approvedYn;
	
	@Column(name = "REJECT_REASON", length = 900)
	private String rejectReason;
	
	@Column(name="APPROVED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDate approvedDate;
	
}
