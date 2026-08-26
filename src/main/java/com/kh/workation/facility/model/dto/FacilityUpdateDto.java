package com.kh.workation.facility.model.dto;

import java.util.List;

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
	
}
