package com.kh.workation.facility.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.facility.model.dto.FacilityRequestDto;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.dto.FacilityUpdateDto;

public interface FacilityService {

	// 1. 전체 시설 목록 조회
	Page<FacilityResponseDto> getFacilityList(Pageable pageable, String sort, String region, String token);
	
	// 2. 시설 목록 검색
	Page<FacilityResponseDto> searchFacilityList(String keyword, Pageable pageable, String sort, String region, String token);
	
	// 3. 특정 시설 상세 조회
	FacilityResponseDto getFacilityById(Long facilityId);
	
	// 4. 시설 등록
	FacilityResponseDto insertFacility(FacilityRequestDto requestDto, MultipartFile[] upfiles, String savePath);
	
	// 5. 시설 수정
	FacilityResponseDto updateFacility(Long facilityId, FacilityUpdateDto updateDto, MultipartFile[] upfiles, String savePath);
	
	// 6. 시설 삭제
	Long deleteFacility(Long facility);
	
	// * 시설 전체 리스트 조회 (워케이션신청용)
	List<FacilityResponseDto> getAllFacilities();
}
