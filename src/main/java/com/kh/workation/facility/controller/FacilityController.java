package com.kh.workation.facility.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.facility.model.dto.FacilityRequestDto;
import com.kh.workation.facility.model.dto.FacilityResponseDto;
import com.kh.workation.facility.model.service.FacilityService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/facilities")
@CrossOrigin
@RequiredArgsConstructor
public class FacilityController {
	
	private final FacilityService facilityService;
	
	// 시설 목록 조회
	@GetMapping
	public ResponseEntity<HashMap<String, Object>> getFacilityList(@RequestParam(value="cpage", defaultValue="1") int currentPage) {
		
		log.info("시설 목록 조회 요청 - cpage: {}", currentPage);
		
		int listLimit = 9; // 한 페이지당 출력할 시설 수
		int pageLimit = 5; // 하단 페이징바 번호 개수
		
		// JPA Pageable 객체 생성
		Pageable pageable = PageRequest.of(currentPage - 1, listLimit);
		
		// 서비스 호출 (Page<FacilityResponseDto> 형태로 변환)
		Page<FacilityResponseDto> page = facilityService.getFacilityList(pageable);
		
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
	@GetMapping("/search")
	public ResponseEntity<HashMap<String, Object>> searchFacilityList(@RequestParam(value="cpage", defaultValue="1") int currentPage, @RequestParam(value="keyword", defaultValue="") String keyword) {
		log.info("시설 검색 요청 - cpage: {}, keyword: {}", currentPage, keyword);
		
		int listLimit = 9;
		int pageLimit = 5;
		
		Pageable pageable = PageRequest.of(currentPage - 1, listLimit);
		
		Page<FacilityResponseDto> page = facilityService.searchFacilityList(keyword, pageable);
		
		List<FacilityResponseDto> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)searchCount, currentPage, pageLimit, listLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("list", list);
		hm.put("pi", pi);
		
		return ResponseEntity.ok(hm);
	}
	
	// 3. 특정 시설 상세 조회
	@GetMapping("/{facilityId}")
	public ResponseEntity<FacilityResponseDto> getFacilityById(@PathVariable("facilityId") Long facilityId) {
		log.info("시설 상세 조회 요청 - ID: {}", facilityId);
		FacilityResponseDto facility = facilityService.getFacilityById(facilityId);
		return ResponseEntity.ok(facility);
	}
	
	// 4. 시설 등록
	@PostMapping
	public ResponseEntity<FacilityResponseDto> insertFacility(@ModelAttribute FacilityRequestDto requestDto, @RequestParam(value = "upfiles", required = false) MultipartFile[] upfiles, HttpServletRequest request) {
		log.info("시설 등록 요청 - Name : {}", requestDto.getFacilityName());
		
		// 웹서버 내 저장 디렉토리 경로
		String savePath = "C:/Final_Project/Workation_BE/uploads/";
		
		FacilityResponseDto result = facilityService.insertFacility(requestDto, upfiles, savePath);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}

