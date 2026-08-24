package com.kh.workation.facility.model.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="FACILITY")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = "imageList")
public class Facility {

	// 필드부
	@Id
	@Column(name="FACILITY_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long facilityId;
	
	@Column(name="FACILITY_TYPE", length=20, nullable=false)
	private String facilityType;
	
	@Column(name="FACILITY_NAME", length=100, nullable=false)
	private String facilityName;
	
	@Column(name="REGION", length=20, nullable=false)
	private String region;
	
	@Column(name="ADDRESS", length=255, nullable=false)
	private String address;
	
	@Column(name="DESCRIPTION", length=900, nullable=true)
	private String description;
	
	@Column(name="STATUS", length=20, nullable=false)
	private String status = "ACTIVE";
	
	@Column(name="ROOM_COUNT", nullable=false)
	private Long roomCount = 0L;
	
	@Column(name="CREATED_DATE", updatable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable=false)
	private LocalDateTime createdDate;
	
	// FACILITY_IMAGE 와의 1 : N 양방향 연관관계 매핑
	@OneToMany(mappedBy = "facility", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<FacilityImage> imageList = new ArrayList<>();
	
	
}
