package com.kh.workation.facility.model.service;

import java.util.List;
import java.util.stream.Collectors;

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
	
	@Override
	public List<FacilityResponseDto> getAllFacilities() {
		List<Facility> facilityList = facilityDao.findAll();
		
		return facilityList.stream()
				.map(FacilityResponseDto::fromEntity)
				.collect(Collectors.toList());
	}
	
	@Override
	public FacilityResponseDto getFacilityById(int facilityId) {
		Facility facility = facilityDao.findById(facilityId).orElseThrow(() -> new IllegalArgumentException("해당 시설을 찾을 수 없습니다. ID : " + facilityId));
		
		return FacilityResponseDto.fromEntity(facility);
	}
	
}
