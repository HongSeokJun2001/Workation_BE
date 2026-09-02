package com.kh.workation.facility.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.facility.model.dto.FacilityRequestDto;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.dto.FacilityUpdateDto;
import com.kh.workation.facility.model.service.FacilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Facility API", description = "시설 관리 API")
@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class FacilityController {
	
	// application.properties에서 업로드 경로 설정 주입
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	private final FacilityService facilityService;
	
	// 시설 목록 조회
	@Operation(summary = "시설 목록 조회", description = "페이징 및 정렬, 지역 조건에 따라 시설 목록을 조회합니다. 토큰 유무에 따라 조회범위가 달라집니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "시설 목록 조회 성공")
	})
	@GetMapping("/facilities")
	public ResponseEntity<HashMap<String, Object>> getFacilityList(@Parameter(description = "현재 페이지 번호", example = "1")
																   @RequestParam(value="cpage", defaultValue="1") int currentPage,
																   @Parameter(description = "정렬 기준 (LATEST 등)", example = "LATEST")
																   @RequestParam(value="sort", defaultValue="LATEST") String sort,
																   @Parameter(description = "지역 필터 조건 (ALL, 서울, 경기, 강원, 제주 등)", example = "강원")
																   @RequestParam(value="region", defaultValue="ALL") String region,
																   @Parameter(description = "인증 토큰", example = "Bearer eyJhbGci...")
																   @RequestHeader(value="Authorization", required = false) String token) {
		
		log.info("시설 목록 조회 요청 - cpage: {}, sort: {}, region: {}, token 존재 여부 : {}", currentPage, sort, token != null);
		
		int listLimit = 9; // 한 페이지당 출력할 시설 수
		int pageLimit = 5; // 하단 페이징바 번호 개수
		
		// JPA Pageable 객체 생성
		Pageable pageable = PageRequest.of(currentPage - 1, listLimit);
		
		// 서비스 호출 (Page<FacilityResponseDto> 형태로 변환)
		Page<FacilityResponseDto> page = facilityService.getFacilityList(pageable, sort, region, token);
		
		List<FacilityResponseDto> list = page.getContent();
		long listCount = page.getTotalElements();
		
		// 공통 Pageination 객체 생성
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, pageLimit, listLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("list", list);
		hm.put("pi", pi);
		
		return ResponseEntity.ok(hm);
		
	}
	
	// 2. 시설 목록 검색 (페이징 적용)
	@Operation(summary = "시설 목록 검색", description = "검색어(키워드)와 정렬 조건을 기준으로 시설 목록을 검색합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "시설 검색 목록 조회 성공")
	})
	@GetMapping("/facilities/search")
	public ResponseEntity<HashMap<String, Object>> searchFacilityList(@Parameter(description = "현재 페이지 번호", example = "1")
																	  @RequestParam(value="cpage", defaultValue="1") int currentPage,
																	  @Parameter(description = "검색할 키워드", example = "회의실")
																	  @RequestParam(value="keyword", defaultValue="") String keyword,
																	  @Parameter(description = "정렬 기준", example = "LATEST")
																	  @RequestParam(value="sort", defaultValue="LATEST") String sort,
																	  @Parameter(description = "지역 필터 조건 (ALL, 서울, 경기, 강원, 제주 등)", example = "ALL")
																	  @RequestParam(value="region", defaultValue="ALL") String region,
																	  @Parameter(description = "인증 토큰", example = "Bearer eyJhGci...")
																	  @RequestHeader(value = "Authorization", required = false) String token) {
		log.info("시설 검색 요청 - cpage: {}, keyword: {}, sort: {}, region: {}", currentPage, keyword, sort, region);
		
		int listLimit = 9;
		int pageLimit = 5;
		
		Pageable pageable = PageRequest.of(currentPage - 1, listLimit);
		
		Page<FacilityResponseDto> page = facilityService.searchFacilityList(keyword, pageable, sort, region, token);
		
		List<FacilityResponseDto> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)searchCount, currentPage, pageLimit, listLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("list", list);
		hm.put("pi", pi);
		
		return ResponseEntity.ok(hm);
	}
	
	// 3. 특정 시설 상세 조회
	@Operation(summary = "시설 상세 조회", description = "시설 ID(PK)를 통해 특정 시설의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description="시설 상세 정보 조회 성공")
	})
	@GetMapping("/facilities/{facilityId}")
	public ResponseEntity<FacilityResponseDto> getFacilityById(@Parameter(description = "조회할 시설 ID", example = "1")
															   @PathVariable("facilityId") Long facilityId) {
		log.info("시설 상세 조회 요청 - ID: {}", facilityId);
		FacilityResponseDto facility = facilityService.getFacilityById(facilityId);
		return ResponseEntity.ok(facility);
	}
	
	// 4. 시설 등록
	@Operation(summary = "시설 등록", description = "시설 정보와 추가 이미지 파일들을 받아 새로운 시설을 등록합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "시설 등록 성공")
	})
	@PostMapping("/facilities")
	public ResponseEntity<FacilityResponseDto> insertFacility(@ModelAttribute FacilityRequestDto requestDto,
															  @Parameter(description = "업로드할 시설 이미지 파일 목록")
															  @RequestParam(value = "upfiles", required = false) MultipartFile[] upfiles) {
		log.info("시설 등록 요청 - Name : {}", requestDto.getFacilityName());
		
		
		FacilityResponseDto result = facilityService.insertFacility(requestDto, upfiles, uploadDir);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	// 5-1. 시설 수정용 상세 정보 조회
	@Operation(summary = "시설 수정용 폼 데이터 조회", description = "시설 수정 화면에 필요한 기존 시설 상세 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "수정 폼 데이터 조회 성공")
	})
	@GetMapping("/facilities/{facilityId}/form")
	public ResponseEntity<FacilityResponseDto> getFacilityForUpdate(@Parameter(description = "수정할 시설 ID", example = "1")
																	@PathVariable("facilityId") Long facilityId) {
		log.info("시설 수정폼 데이터 조회 요청 - ID : {}", facilityId);
		FacilityResponseDto facility = facilityService.getFacilityById(facilityId);
		return ResponseEntity.ok(facility);
	}
	
	// 5-2. 시설 정보 수정
	@Operation(summary = "시설 정보 수정", description = "시설 정보를 수정하고 삭제할 기존 이미지 및 신규 이미지를 처리합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "시설 수정 성공")
	})
	@PutMapping("/facilities/{facilityId}")
	public ResponseEntity<FacilityResponseDto> updateFacility(@Parameter(description = "수정할 시설 ID", example = "1")
															  @PathVariable("facilityId") Long facilityId, 
															  @ModelAttribute FacilityUpdateDto updateDto,
															  @Parameter(description = "새로 추가할 시설 이미지 파일 목록")
															  @RequestParam(value = "upfiles", required = false) MultipartFile[] upfiles) {
		log.info("시설 수정 요청 (PUT) - ID: {}, Name: {}", facilityId, updateDto.getFacilityName());
		FacilityResponseDto result = facilityService.updateFacility(facilityId, updateDto, upfiles, uploadDir);
		return ResponseEntity.ok(result);
	}
	
	// 6. 시설 삭제
	@Operation(summary = "시설 삭제", description = "시설 ID를 받아 해당 시설의 상태를 비활성화(INACTIVE) 처리합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "시설 삭제 성공")
	})
	@DeleteMapping("/facilities/{facilityId}")
	public ResponseEntity<Long> deleteFacility(@Parameter(description = "삭제할 시설 ID", example = "1")
											   @PathVariable("facilityId") Long facilityId) {
		log.info("시설 삭제 요청 - ID : {}", facilityId);
		
		Long deletedId = facilityService.deleteFacility(facilityId);
		
		return ResponseEntity.ok(deletedId);
	}
	
	// * 시설 전체 리스트 조회 (워케이션신청용)
	@GetMapping("/facilities/all")
	public ResponseEntity<List<FacilityResponseDto>> getAllFacilities() {
	    List<FacilityResponseDto> list = facilityService.getAllFacilities();
	    return ResponseEntity.ok(list);
	}
}

