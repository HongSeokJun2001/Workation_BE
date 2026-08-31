package com.kh.workation.facility.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.facility.model.dao.FacilityDao;
import com.kh.workation.facility.model.dto.FacilityRequestDto;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.dto.FacilityUpdateDto;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.facility.model.vo.FacilityImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class FacilityServiceImpl implements FacilityService{

	private final FacilityDao facilityDao;
	private final AuthService authService;	
	
	// 토큰에서 최고관리자(SUPER) 여부 판단 헬퍼 메서드
	private boolean isSuperAdmin(String token) {
		if(token == null || !token.startsWith("Bearer")) {
			return false;
		}
		// "Bearer" 제거 후 토큰 파싱
		String rawToken = token.substring(7);
		
		// AuthService의 isSuperAdminToken 메서드 호출
		return authService.isSuperAdminToken(rawToken);
	}

	// 1. 전체 시설 목록 조회
	@Override
	public Page<FacilityResponseDto> getFacilityList(Pageable pageable, String sort, String token) {
		Page<Facility> facilityPage;
		boolean isOldest = "OLDEST".equalsIgnoreCase(sort);
		
		if(isSuperAdmin(token)) {
			// 최고관리자: ACTIVE + INACTIVE 전체 조회
			facilityPage = isOldest ? facilityDao.findAllFacilitiesForAdminAsc(pageable)
									: facilityDao.findAllFacilitiesForAdminDesc(pageable);
		} else {
			// 일반사용자 / 비로그인: ACITVE 상태만 조회
			facilityPage = isOldest ? facilityDao.findAllActiveFacilitiesAsc(pageable)
									: facilityDao.findAllActiveFacilitiesDesc(pageable);
		}
		
		return facilityPage.map(FacilityResponseDto::fromEntity);
	}

	// 2. 시설 목록 검색
	@Override
	public Page<FacilityResponseDto> searchFacilityList(String keyword, Pageable pageable, String sort, String token) {
		Page<Facility> facilityPage;
		boolean isOldest = "OLDEST".equalsIgnoreCase(sort);
		if(isSuperAdmin(token)) {
			// 최고관리자:전체 상태 대상 검색
			facilityPage = isOldest ? facilityDao.searchFacilitiesForAdminAsc(keyword,pageable)
									: facilityDao.searchFacilitiesForAdminDesc(keyword,pageable);
		} else {
			//일반 사용자 / 비로그인: ACTIVE 상태만 검색
			facilityPage = isOldest ? facilityDao.searchActiveFacilitiesAsc(keyword, pageable)
									: facilityDao.searchActiveFacilitiesDesc(keyword, pageable);
		}
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

	// 5. 시설 수정 서비스
	@Override
	@Transactional
	public FacilityResponseDto updateFacility(Long facilityId, FacilityUpdateDto updateDto, MultipartFile[] upfiles,
			String savePath) {
		
		// 기존 시설 엔터티 조회
		Facility facility = facilityDao.findById(facilityId).orElseThrow(() -> new IllegalArgumentException("해당 시설을 찾을 수 없습니다. ID : " + facilityId));
		
		// 기본 정보 업데이트 (JPA Dirty Checking)
		facility.setFacilityName(updateDto.getFacilityName());
		facility.setFacilityType(updateDto.getFacilityType());
		facility.setRegion(updateDto.getRegion());
		facility.setAddress(updateDto.getAddress());
		facility.setRoomCount(updateDto.getRoomCount());
		facility.setDescription(updateDto.getDescription());
		if(updateDto.getStatus() != null) {
			facility.setStatus(updateDto.getStatus());
		}
		
		// 기존 이미지 중 삭제 요청된 이미지 제거
		if(updateDto.getDeleteImageIds() != null && !updateDto.getDeleteImageIds().isEmpty()) {
			facility.getImageList().removeIf(image -> {
				boolean isDelete = updateDto.getDeleteImageIds().contains(image.getImageId());
				if(isDelete) {
					// 실제 디스크 파일 삭제 처리
					File file = new File(savePath + image.getChangedName());
					if(file.exists()) {
						file.delete();
					}
				}
				return isDelete;
			});
		}
		
		// 신규 첨부 파일 추가 업로드
		if(upfiles != null && upfiles.length > 0) {
			File dir = new File(savePath);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			for(MultipartFile file : upfiles) {
				if(!file.isEmpty()) {
					String originName = file.getOriginalFilename();
					String ext = originName.substring(originName.lastIndexOf("."));
					String changeName = UUID.randomUUID().toString() + ext;
					
					try {
						File destFile = new File(dir, changeName);
						file.transferTo(destFile);
						
						FacilityImage facilityImage = new FacilityImage();
						facilityImage.setOriginalName(originName);
						facilityImage.setChangedName(changeName);
						facilityImage.setFilePath("/uploads/" + changeName);
						
						// 연관관계 편의 메서드로 저장
						facility.addImage(facilityImage);
					} catch(IOException e) {
						throw new RuntimeException("파일 수정 저장 실패 : " + originName, e);
					}
				}
			}
		}
		
		return FacilityResponseDto.fromEntity(facility);
	}

	// 6. 시설 삭제 서비스
	@Override
	@Transactional
	public Long deleteFacility(Long facilityId) {
		// 기존 시설 엔터티 조회
		Facility facility = facilityDao.findById(facilityId)
				.orElseThrow(() -> new IllegalArgumentException("해당 시설을 찾을 수 없습니다."));
		
		// 상태값을 INACTIVE 상태로 전환
		facility.setStatus("INACTIVE");
		
		return facility.getFacilityId();
	}
	
	
	// * 시설 전체 리스트 조회 (워케이션신청용)
	@Override
	@Transactional(readOnly = true)
	public List<FacilityResponseDto> getAllFacilities() {
	    List<Facility> facilityList = facilityDao.findAll();
	    
	    return facilityList.stream()
	            .map(FacilityResponseDto::fromEntity)
	            .collect(Collectors.toList());
	}
}
