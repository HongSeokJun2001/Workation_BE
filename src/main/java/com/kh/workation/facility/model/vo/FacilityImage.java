package com.kh.workation.facility.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name="FACILITY_IMAGE")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString(exclude = "facility") // 양방향 매핑 시 순환 참조 방지

public class FacilityImage {

	@Id
	@Column(name="IMAGE_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long imageId;
	
	// FACILITY ㅔ이블과의 N:1 연관관계 매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FACILITY_ID", nullable=false)
	@JsonIgnore // JSON 직렬화 시 무한 루프 방지
	private Facility facility;
	
	@Column(name="ORIGINAL_NAME", length=255, nullable=false)
	private String originalName;
	
	@Column(name="CHANGED_NAME", length=255, nullable=false)
	private String changedName;
	
	@Column(name="FILE_PATH", length=900, nullable=false)
	private String filePath;
	
	@CreationTimestamp
	@Column(name="CREATED_DATE", updatable=false, nullable=false)
	private LocalDateTime createdDate;
	
}
