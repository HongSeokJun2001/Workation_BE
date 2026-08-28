package com.kh.workation.crew.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.member.model.vo.Employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
public class CrewController {
	
	
	@Autowired
	private CrewService crewService;
	
	
	
	
	// 크루 리스트 전체 조회
	@Operation(summary="게시글 목록 조회 (페이징)", description="페이지 번호 (cpage) 에 해당하는 게시글 목록을 조회합니다. " 
			+ "응답 : {list : 게시글목록, pi : 페이지정보}")
	@ApiResponse(responseCode="200", description="조회 성공",
				content=@Content(mediaType="application/json",
				examples=@ExampleObject(value="""
											{
												"list" : [{}, {}, {}],
												"pi" : {
													"listCount" : 42,
													"currentPage" : 1,
													"pageLimit" : 5,
													"boardLimit" : 5, 
													"maxPage" : 9,
													"startPage" : 1,
													"endPage" : 5
												}
											}
											""")))
	@GetMapping("/crews")
	public ResponseEntity<HashMap<String, Object>> selectCrewList(
													@RequestParam(value="cpage", defaultValue="1") 
													int currentPage ){
		// 한페이지당 몇개씩 보여질건지, 페이징바 숫자 갯수
		int boardLimit = 5;
		int pageLimit = 5;
		
		
		Pageable pageable = PageRequest.of(currentPage -1, 5);
		
		//페이지 객체로 받아오기
		Page<Crew> page = crewService.selectCrewList(pageable);
		
		//Page 객체로부터 조회된 총 게시글 갯수 
		List<Crew> list = page.getContent();
		
		//Count함수 실행
		long listCount = page.getTotalElements();
		
		// > PageInfo 객체 생성
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("list", list); // list에 출력할 용도
		hm.put("pi", pi);
		
				
		return ResponseEntity.status(HttpStatus.OK).body(hm);
		
	}
	
	// 크루 검색
	@GetMapping("crews/search")
	public ResponseEntity<HashMap<String, Object>> searchCrewList(
													@RequestParam(value="cpage", defaultValue="1")int currentPage, 
													String keyword){
		
		// 	검색도 페이징 처리 될 수 있게 설정
		int boardLimit = 5;
		int pageLimit = 5;
		
		// > pageable 객체 생성
		Pageable pageable = PageRequest.of(currentPage-1, boardLimit);
		
		// pageable 을 넘기면서 조회
		Page<Crew> page = crewService.searchCrewList(keyword, pageable);
		
		List<Crew> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.
					getPageInfo((int)boardLimit, currentPage, pageLimit, pageLimit);
		
		HashMap<String,Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list",list);
		
		return ResponseEntity.status(HttpStatus.OK).body(hm);
		
	}
	
	
	
	// 크루 등록
	@Operation(summary="크루 모집 글 작성",description="크루 모집 글을 작성합니다. JWT 토큰에서 작성자 정보를 추출하므로 로그인이 필요합니다.")
	@ApiResponse(responseCode="200", description = "body로success/fail응답")
	@SecurityRequirement(name="JWT")
	@PostMapping("/crews")
	public ResponseEntity<String> insertCrew(@RequestBody Crew c,
											HttpServletRequest request){
		
		// 1. 작성자(크루장) 역할을 할 임시 Employee 객체 생성
	    Employee tempEmployee = new Employee();
	    
	    // 2. 크루장 사원 PK ID 세팅 (Employee의 PK 타입에 맞춰 설정하세요)
	    // Integer인 경우: 1
	    // Long인 경우: 1L
	    tempEmployee.setEmployeeId(1L); // 또는 tempEmployee.setEmpId(1); 등 Employee VO의 PK setter명
	    
	    // 3. Crew 엔티티의 employee 필드에 세팅
	    c.setEmployee(tempEmployee);
		
		Crew insertCr = crewService.insertCrew(c);
		
		String message = (insertCr != null) ? "success" : "fail";
		
		
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
	
	
	// 크루 수정
	
	// 크루 삭제
	
	// 크루 검색
	
	
	
	
	
	// 댓글 조회
	
	// 댓글 작성
	
	// 댓글 삭제
	
	
	
	
	
	
	
	// 크루 신청버튼 클릭
	
	// 크루 탈퇴
	
	

}
