package com.kh.workation.crew.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.member.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="CREW_MEMBER_HIST")

@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CrewMemberHist {
	
	@Id
	@Column(name="crewMemberId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer crewMemberId;
	
	@JoinColumn(name="CREW_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Crew crew;
	
	@JoinColumn(name="EMPLOYEE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee;
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")	
	private LocalDateTime joinedDate;
	
	@Column(name="LEFT_DATE")
	private LocalDateTime leftDate;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'ACTIVE'")	
	private String status;

}
