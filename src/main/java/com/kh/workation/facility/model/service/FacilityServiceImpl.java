package com.kh.workation.facility.model.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.facility.model.dao.FacilityDao;
import com.kh.workation.facility.model.dto.FacilityRequestDto;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.facility.model.vo.FacilityImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class FacilityServiceImpl implements FacilityService{

	private final FacilityDao facilityDao;

	// 1. 전체 시설 목록 조회
	@Override
	public Page<FacilityResponseDto> getFacilityList(Pageable pageable) {
		Page<Facility> facilityPage = facilityDao.findAllFacilities(pageable);
		
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

	// 4. 시설 등록 서비스
	@Override
	@Transactional
	public FacilityResponseDto insertFacility(FacilityRequestDto requestDto, MultipartFile[] upfiles, String savePath) {
		
		// 1) DTO -> Entity 변환 및 DB 1차 저장
		Facility facility = requestDto.toEntity();
		Facility savedFacility = facilityDao.save(facility);
		
		// 2) 첨부 이미지 파일 등록 처리
		if(upfiles != null && upfiles.length > 0) {
			File dir = new File(savePath);
			if(!dir.exists()) {
				dir.mkdirs(); // 디랙토리가 없는 경우 생성
			}
			
			for (MultipartFile file : upfiles) {
				if(!file.isEmpty()) {
					String originName = file.getOriginalFilename();
					String ext = originName.substring(originName.lastIndexOf("."));
					String changeName = UUID.randomUUID().toString() + ext; // 파일명 중복 방지
					
					try {
						
						File destFile = new File(dir, changeName);
						file.transferTo(destFile);
						
						// FacilityImage Entity 생성
						FacilityImage facilityImage = new FacilityImage();
						facilityImage.setOriginalName(originName);
						facilityImage.setChangedName(changeName);
						facilityImage.setFilePath("/uploads/" + changeName);
						
						// 양방향 편의 메서드로 연관관계 설정
						savedFacility.addImage(facilityImage);
					} catch (IOException e) {
						throw new RuntimeException("파일 저장 실패 : " + originName, e);
					}
				}
			}
		}
		
		return FacilityResponseDto.fromEntity(savedFacility);
	}
	
	
	
}
