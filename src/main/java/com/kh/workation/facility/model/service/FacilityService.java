package com.kh.workation.facility.model.service;

import java.util.List;

import com.kh.workation.facility.model.dto.FacilityResponseDto;

public interface FacilityService {

	// 전체 시설 목록 조회
	List<FacilityResponseDto> getAllFacilities();
	
	// 특정 시설 상세 조회
	FacilityResponseDto getFacilityById(int facilityId);
	
}
