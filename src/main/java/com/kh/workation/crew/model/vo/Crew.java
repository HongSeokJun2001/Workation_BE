package com.kh.workation.crew.model.vo;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.member.model.vo.Company;
import com.kh.workation.member.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="CREW")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Crew {

	@Id
	@Column(name="CREW_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Schema(description = "크루 고유 번호", example = "1")
	private Integer crewId; // 쿠르ID(PK)
	
	@JoinColumn(name="COMPANY_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company; 	//회사ID(FK) 
	
	@JoinColumn(name="LEADER_EMPLOYEE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee; //크루장(직원)ID 
	
	@Column(name="CREW_NAME", nullable=false)
	@Schema(description = "크루명", example = "제주 워케이션 크루")
	private String crewName;	//크루명
	
	@Column(name="CREW_CONTENT", length=800)
	@Schema(description = "크루 소개", example = "제주에서 함께 일하고 여행해요.")
	private String crewContent;		//소개내용
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'Y'")
	@Schema(description = "모집 상태", example = "Y", allowableValues = {"Y", "N"})
	private String status;	//상태
	
	
	@Column(name="CREATED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Schema(description = "모집 시작일", example = "2026-09-08")
	private LocalDate createdDate;	//생성일
	
	@Column(name="END_DATE")
	@Schema(description = "모집 마감일", example = "2026-09-30")
	private LocalDate endDate; //마감일
	
	@Column(name="CAPACITY", nullable=false, columnDefinition="INT DEFAULT 0")
	@Schema(description = "모집 정원", example = "5")
	private Integer capacity; //정원 
	
	@Column(name="WORK_USED_DAYS", nullable=false, columnDefinition="INT DEFAULT 1")
	@Schema(description = "워케이션 사용 일수", example = "3")
	private Integer workUsedDays;


	//Jackson이 양방향 연관관계를 JSON으로 변환할 때 순환참조를 막아주는 어노테이션
	@OneToMany(mappedBy = "crew", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<CrewMemberHist> crewMemberHists;
	
}
