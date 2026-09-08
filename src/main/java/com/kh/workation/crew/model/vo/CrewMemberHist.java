package com.kh.workation.crew.model.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="CREW_MEMBER_HIST")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "crewMemberId")

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
	@Schema(description = "가입 이력 고유 번호", example = "1")
	private Integer crewMemberId;


	//Jackson이 양방향 연관관계를 JSON으로 변환할 때 순환참조를 막아주는 어노테이션
	@JoinColumn(name="CREW_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Crew crew;
	
	@JoinColumn(name="EMPLOYEE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee;
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")	
	@Schema(description = "가입일시", example = "2026-09-08T10:00:00")
	private LocalDateTime joinedDate;
	
	@Column(name="LEFT_DATE")
	@Schema(description = "탈퇴일시", example = "2026-09-10T15:00:00", nullable = true)
	private LocalDateTime leftDate;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'ACTIVE'")	
	@Schema(description = "가입 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "LEFT"})
	private String status;

}
