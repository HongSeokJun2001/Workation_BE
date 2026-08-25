package com.kh.workation.facility.model.dto;

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
