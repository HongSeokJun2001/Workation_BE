package com.kh.workation.facility.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.service.FacilityService;

import lombok.RequiredArgsConstructor;
/*
@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
	private final FacilityService facilityService;
	
	// 전체 시설 목록 조회
	@GetMapping
	public ResponseEntity<List<FacilityResponseDto>> getAllFacilities() {
		List<FacilityResponseDto> list = facilityService.getAllFacilities();
		return ResponseEntity.ok(list);
	}
	
	// 특정 시설 상세 조회
	@GetMapping("/{facilityId}")
	public ResponseEntity<FacilityResponseDto> getFacilityById(@PathVariable("facilityId") int facilityId) {
		FacilityResponseDto facility = facilityService.getFacilityById(facilityId);
		return ResponseEntity.ok(facility);
	}
}
*/
