package com.kh.workation.crew.model.vo;

import java.time.LocalDate;
import java.util.List;

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
	private Integer crewId; // 쿠르ID(PK)
	
	@JoinColumn(name="COMPANY_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company; 	//회사ID(FK) 
	
	@JoinColumn(name="LEADER_EMPLOYEE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee; //크루장(직원)ID 
	
	@Column(name="CREW_NAME", nullable=false)
	private String crewName;	//크루명
	
	@Column(name="CREW_CONTENT", length=800)
	private String crewContent;		//소개내용
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'Y'")
	private String status;	//상태
	
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDate createdDate;	//생성일
	
	@Column(name="END_DATE")
	private LocalDate endDate; //마감일
	
	@Column(name="CAPACITY", nullable=false, columnDefinition="INT DEFAULT 0")
	private Integer capacity; //정원 
	
	@Column(name="WORK_USED_DAYS", nullable=false, columnDefinition="INT DEFAULT 1")
	private Integer workUsedDays;
	
	@OneToMany(mappedBy = "crew", fetch = FetchType.LAZY)
	private List<CrewMemberHist> crewMemberHists;
	

}
