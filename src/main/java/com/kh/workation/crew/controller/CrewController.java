package com.kh.workation.crew.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.crew.model.dto.CrewResponse;
import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.reply.model.vo.Reply;

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
	
	@Autowired
	private AuthService authService;
	
	
	
	
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
	
	//	크루 단건 조회
	@GetMapping("/crews/{crewId}")
	public ResponseEntity<Crew> selectCrew(@PathVariable int crewId){
		
		Crew c = crewService.selectCrew(crewId);
		
		return ResponseEntity.status(HttpStatus.OK).body(c);
		
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
	@PutMapping("/crews/{crewId}")
	public ResponseEntity<String> updateCrew(@PathVariable("crewId")int crewId, @RequestBody Crew c, HttpServletRequest request){
		
		Crew updateCr = crewService.updateCrew(c);
		
		String message = (updateCr != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK).body(message);
		
		
	}
	
	
	
	
	// 크루 삭제
	@DeleteMapping("/crews/{crewId}")
	public ResponseEntity<String> deleteCrew(@PathVariable("crewId")int crewId){
		
		int result = crewService.deleteCrew(crewId);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK).body(message);
		
	}
	
	
	// 크루신청	
	@PostMapping("/crews/{crewId}/join")
	public ResponseEntity<String> joinCrew(@PathVariable int crewId,
											HttpServletRequest request){
		
		// 1. 작성자(크루장) 역할을 할 임시 Employee 객체 생성
	    Employee tempEmployee = new Employee();
	    
	    // 2. 크루장 사원 PK ID 세팅 (Employee의 PK 타입에 맞춰 설정하세요)
	    // Integer인 경우: 1
	    // Long인 경우: 1L
	    tempEmployee.setEmployeeId(1L); // 또는 tempEmployee.setEmpId(1); 등 Employee VO의 PK setter명
	    
	    // 3. Crew 엔티티의 employee 필드에 세팅
	    CrewMemberHist cm = new CrewMemberHist();
	    cm.setEmployee(tempEmployee);
	    cm.setCrew(crewService.selectCrew(crewId));
	    
	    
	   CrewMemberHist crewMem = crewService.joinCrew(cm);
	   
	   String message = (crewMem != null) ? "success" : "fail";
	   
	   return ResponseEntity.status(HttpStatus.OK).body(message);
		
	}

	// 내가 신청한 크루 목록 조회
	@GetMapping("/crews/mylist/{employeeId}")
	public ResponseEntity<List<CrewMemberHist>> selectMyCrewList(
			@PathVariable Long employeeId) {
		return ResponseEntity.ok(crewService.selectMyCrewList(employeeId));
	}

	// 크루 탈퇴
	@DeleteMapping("/crews/{crewId}/join")
	public ResponseEntity<String> leaveCrew(@PathVariable int crewId) {
		int result = crewService.leaveCrew(1L, crewId);
		//crewId나중에 수
		return ResponseEntity.ok(result > 0 ? "success" : "fail");
	}
	
	// 댓글 작성
	
	// 댓글 삭제
	
	
	
	
	
	
	
	// 크루 신청버튼 클릭
	
	// 크루 탈퇴
	
	
	// 내가 신청한 크루 목록 조
	
	

	// * 워케이션신청용 크루불러오기 코드
	@GetMapping("/crews/leader")
	public ResponseEntity<List<CrewResponse>> getMyLeaderCrews(HttpServletRequest request) {
	    
		String authHeader = request.getHeader("Authorization");
    
        String token = authHeader.substring(7);

        String loginId = authService.getLoginId(token);
        System.out.println("조회하려는 loginId = " + loginId);
        
        List<Crew> crewList = crewService.getLeaderCrews(loginId);
        
        List<CrewResponse> responseList = crewList.stream()
                .map(CrewResponse::new)
                .toList();

        return ResponseEntity.ok(responseList);
	}
}
