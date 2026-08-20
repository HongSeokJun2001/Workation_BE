package com.kh.workation.member.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="COMPANY")

@DynamicInsert
@DynamicUpdate


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Company {
	
	@Id
	@Column(name="COMPANY_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int companyId; //회사아이디(PK)
	
	@Column(name="COMPANY_NAME")
	private String companyName; //회사명
	
	//길이 제한 따로 안함 기본 255
	@Column(name="BUSINESS_NO", nullable=false, unique = true)
	private int businessNo; //사업자번호 NOTNULL 유니크제약조건
	
	@Column(name="COMPANY_STATUS", nullable=false, columnDefinition="DEFAULT 'Y'")
	private String companyStatus; // 상태
	
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate; //생성일

}
