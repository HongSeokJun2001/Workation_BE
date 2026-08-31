package com.kh.workation.facility.model.dto;

import java.util.List;

import com.kh.workation.common.template.XssDefencePolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityUpdateDto {

	private String facilityName;
	private String facilityType;
	private String region;
	private String address;
	private Long roomCount;
	private String description;
	private String status;
	
	// 기존 이미지 중 삭제 대상 파일의 ID 목록
	private List<Long> deleteImageIds;
	
	// XSS 방어 처리
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName != null ? XssDefencePolicy.defence(facilityName) : null;
	}
	
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType != null ? XssDefencePolicy.defence(facilityType) : null;
	}
	
	public void setRegion(String region) {
		this.region = region != null ? XssDefencePolicy.defence(region) : null;
	}
	
	public void setAddress(String address) {
		this.address = address != null ? XssDefencePolicy.defence(address) : null;
	}
	
	public void setDescription(String description) {
		this.description = description != null ? XssDefencePolicy.defence(description) : null;
	}
	
	public void setStatus(String status) {
		this.status = status != null ? XssDefencePolicy.defence(status) : null;
	}
	
}
