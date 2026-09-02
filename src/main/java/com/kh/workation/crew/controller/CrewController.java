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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.crew.model.dto.CrewResponse;
import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.member.model.vo.Admin;
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
	
	//JWT 공통 처리
	
	// Authorization Header에서 JWT를 추출하고 유효한 토큰인지 확인
    private String getToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        return authService.isValidToken(token) ? token : null;
    }
    
    
 // SUPER_ADMIN 권한 확인
    private boolean isSuperAdmin(String token) {
        return token != null && authService.isSuperAdminToken(token);
    }

    private boolean isCrewLeader(String token, Crew crew) {
        if (token == null || crew == null) {
            return false;
        }

        String loginId = authService.getLoginId(token);
        if (loginId == null || crew.getEmployee() == null) {
            return false;
        }

        return loginId.equals(crew.getEmployee().getLoginId());
    }
    
    
	
	
	// 크루 리스트 전체 조회
    // SUPER / COMPANY / EMPLOYEE 모두 조회 가능
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
													int currentPage,
													@RequestHeader(value = "Authorization", required = false) String authHeader){
		// 로그인 여부 확인
        if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		// 한페이지당 몇개씩 보여질건지, 페이징바 숫자 갯수
		int boardLimit = 5;
		int pageLimit = 5;
		
		
		Pageable pageable = PageRequest.of(currentPage -1, boardLimit);
		
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
	// 로그인 한 모든 사용자 조회 가능 
	@GetMapping("/crews/{crewId}")
	public ResponseEntity<Crew> selectCrew(@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		
		Crew c = crewService.selectCrew(crewId);
		
		return ResponseEntity.status(HttpStatus.OK).body(c);
		
	}
	
	// 크루 검색
	//	로그인한 모든 사용자가 검색 가능 
	@GetMapping("crews/search")
	public ResponseEntity<HashMap<String, Object>> searchCrewList(
													@RequestParam(value="cpage", defaultValue="1")int currentPage, 
													@RequestParam String keyword,
													@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		
		
		if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		
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
	// EMPLOYEE만 가능 
	@Operation(summary="크루 모집 글 작성",description="크루 모집 글을 작성합니다. JWT 토큰에서 작성자 정보를 추출하므로 로그인이 필요합니다.")
	@ApiResponse(responseCode="200", description = "body로success/fail응답")
	@SecurityRequirement(name="JWT")
	@PostMapping("/crews")
	public ResponseEntity<String> insertCrew(@RequestBody Crew c,
										HttpServletRequest request,
										@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		 String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }
        
        
        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        if (c == null) {
            return ResponseEntity.badRequest().body("fail");
        }

        String loginId = authService.getLoginId(token);

        // TODO: loginId로 실제 Employee를 조회하여 작성자로 설정
        // crewService.insertCrew(c, loginId);

        Crew result = crewService.insertCrew(c);

        return ResponseEntity.ok(result != null ? "success" : "fail");
	}
	
	
	
	// 크루 수정
	// 해당 Crew의 작성자만 가능
	@PutMapping("/crews/{crewId}")
	public ResponseEntity<String> updateCrew(@PathVariable("crewId")int crewId, @RequestBody Crew c,
										HttpServletRequest request,
										@RequestHeader(value = "Authorization", required = false) String authHeader){
		 String token = getToken(authHeader);

	    // JWT가 없는 경우
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
	    }

	    // 기존 Crew 조회
	    Crew existingCrew = crewService.selectCrew(crewId);

	    // 존재하지 않는 Crew
	    if (existingCrew == null) {
	        return ResponseEntity.notFound().build();
	    }

	    // 관리자도 아니고 해당 Crew 작성자도 아닌 경우
	    if (!isSuperAdmin(token) && !isCrewLeader(token, existingCrew)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
	    }

	    // 수정 데이터가 없는 경우
	    if (c == null) {
	        return ResponseEntity.badRequest().body("fail");
	    }

	    // URL의 crewId를 사용
	    c.setCrewId(crewId);

	    // 작성자 정보는 기존 데이터 유지
	    if (c.getEmployee() == null) {
	        c.setEmployee(existingCrew.getEmployee());
	    }

	    // 수정
        Crew updateCr = crewService.updateCrew(c);
        return ResponseEntity.ok(updateCr != null ? "success" : "fail");
        
        
        }
	// 해당 Crew의 작성자만 가능
	
	@DeleteMapping("/crews/{crewId}")
	public ResponseEntity<String> deleteCrew(@PathVariable("crewId")int crewId,
										@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

	    // JWT가 없는 경우
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
	    }

	    // 기존 Crew 조회
	    Crew existingCrew = crewService.selectCrew(crewId);

	    // 존재하지 않는 Crew
	    if (existingCrew == null) {
	        return ResponseEntity.notFound().build();
	    }

	    // 관리자도 아니고 해당 Crew 작성자도 아닌 경우
	    if (!isSuperAdmin(token) && !isCrewLeader(token, existingCrew)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
	    }

	    // 삭제
	    int result = crewService.deleteCrew(crewId);
        return ResponseEntity.ok(result > 0 ? "success" : "fail");
            		
            		
	}      		
            		
            		
            		
	@PostMapping("/crews/{crewId}/join")
	public ResponseEntity<String> joinCrew(@PathVariable int crewId,
											HttpServletRequest request,
											@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
	    }

	    // 일반 직원만 신청 가능
	    if (!authService.isEmployeeToken(token)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
	    }

	    String loginId = authService.getLoginId(token);

	    // TODO: loginId를 이용해 실제 Employee를 조회해야 함
	    // crewService.joinCrew(crewId, loginId);

	    CrewMemberHist result = crewService.joinCrew(crewId, loginId);

	    return ResponseEntity.ok(result != null ? "success" : "fail");
		
	}

	// 내가 신청한 크루 목록 조회
	// EMPLOYEE
	@GetMapping("/crews/mylist")
	public ResponseEntity<List<CrewMemberHist>> selectMyCrewList(
		@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 로그인한 직원의 loginId를 JWT에서 가져옴
        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        
        String loginId = authService.getLoginId(token);

        return ResponseEntity.ok(
                crewService.selectMyCrewList(loginId)
        );
	}

	// 크루 탈퇴
	@DeleteMapping("/crews/{crewId}/join")
	public ResponseEntity<String> leaveCrew(@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		
		 String token = getToken(authHeader);

		    if (token == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
		    }

		    // 일반 직원만 탈퇴 가능
		    if (!authService.isEmployeeToken(token)) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
		    }

		    String loginId = authService.getLoginId(token);

		    int result = crewService.leaveCrew(crewId, loginId);

		    return ResponseEntity.ok(result > 0 ? "success" : "fail");
		
	}
	
	
	
	
	
	
	
	
	

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
