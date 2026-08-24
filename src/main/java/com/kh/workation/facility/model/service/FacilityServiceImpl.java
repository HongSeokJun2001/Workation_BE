package com.kh.workation.facility.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.facility.model.dao.FacilityDao;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.vo.Facility;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class FacilityServiceImpl implements FacilityService{

	private final FacilityDao facilityDao;

	// 1. 전체 시설 목록 조회
	@Override
	public Page<FacilityResponseDto> getFacilityList(Pageable pageable) {
		Page<Facility> facilityPage = facilityDao.findAll(pageable);
		
		// Page<Facility> -> Page<FacilityResponseDto> 자동 매핑
		return facilityPage.map(FacilityResponseDto::fromEntity);
	}

	// 2. 시설 목록 검색
	@Override
	public Page<FacilityResponseDto> searchFacilityList(String keyword, Pageable pageable) {
		Page<Facility> facilityPage = facilityDao.findByFacilityNameContainingOrRegionContaining(keyword, keyword, pageable);
		
		return facilityPage.map(FacilityResponseDto::fromEntity);
	}

	// 3. 특정 시설 상세 조회
	@Override
	public FacilityResponseDto getFacilityById(Long facilityId) {
		Facility facility = facilityDao.findById(facilityId)
				.orElseThrow(() -> new IllegalArgumentException("해당 시설을 찾을 수 없습니다. ID : " + facilityId));
		
		return FacilityResponseDto.fromEntity(facility);
	}
	
	
	
}
