package com.kh.workation.facility.model.dto;

import com.kh.workation.common.template.XssDefencePolicy;
import com.kh.workation.facility.model.vo.Facility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityRequestDto {

	private String facilityName;
	private String facilityType;
	private String region;
	private String address;
	private Long roomCount;
	private String description;
	
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
	
	// Dto -> Entity 변환 메서드
	public Facility toEntity() {
		Facility facility = new Facility();
		facility.setFacilityName(this.facilityName);
		facility.setFacilityType(this.facilityType);
		facility.setRegion(this.region);
		facility.setAddress(this.address);
		facility.setRoomCount(this.roomCount);
		facility.setDescription(this.description);
		facility.setStatus("ACTIVE");
		return facility;
	}
	
}
