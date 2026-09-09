package com.kh.workation.crew.controller;

import java.time.LocalDate;
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
import com.kh.workation.common.template.XssDefencePolicy;
import com.kh.workation.crew.model.dto.CrewResponse;
import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;



/*
 * [CrewController]
 * 
 * 크루 모집 커뮤니티 관련된 HTTP 요청을 처리하는 Controller
 * 
 * 주요 기능
 * 1. 크루 목록 조회
 * 2. 크루 상세 조회
 * 3. 크루 검색
 * 4. 크루 작성 / 수정 / 삭제
 * 5. 크루 가입 / 탈퇴
 * 6. 내가 가입한 크루 조회
 * 7. 내가 만든 크루 조회
 * 8. 크루 멤버 조회
 * 
 * 
 */


@CrossOrigin
@RestController
@Tag(name = "Crew API", description = "크루 모집 및 가입 관련 API")
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
    
    /*
     * [수정]
     * 모든 로그인 사용자가 사용하는 JWT 인증 여부 확인
     *
     * 기존 코드에서는 각 메서드마다
     *
     * if (getToken(authHeader) == null) {
     *     ...
     * }
     *
     * 를 반복하고 있었기 때문에 메서드로 분리
     */
    private boolean isAuthenticated(String authHeader) {
        return getToken(authHeader) != null;
    }
       
    
    
    // SUPER_ADMIN 권한 확인
    private boolean isSuperAdmin(String token) {
        return token != null && authService.isSuperAdminToken(token);
    }
    
    /*
     * 일반 EMPLOYEE 권한 확인
     */
    private boolean isEmployee(String token) {
        return token != null && authService.isEmployeeToken(token);
    }
    
    /*
     * 크루 작성자인지 확인
     *
     * JWT에서 로그인한 사용자의 loginId를 가져온 뒤
     * 해당 Crew의 작성자 loginId와 비교한다.
     */
    
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
    
    
	
	
	// ********* 크루 리스트 전체 조회 *************
    // SUPER / COMPANY / EMPLOYEE 모두 조회 가능
    // cpage : 현재 페이지 / sort  : 정렬 기준
	@Operation(summary="크루 목록 조회 (페이징)", description="로그인한 사용자가 크루 모집 목록을 페이징하여 조회합니다." 
			+ "응답 : {list : 게시글목록, pi : 페이지정보}")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "list": [{}, {}, {}],
                        "pi": {
                            "listCount": 42,
                            "currentPage": 1,
                            "pageLimit": 5,
                            "boardLimit": 6,
                            "maxPage": 7,
                            "startPage": 1,
                            "endPage": 5
                        }
                    }
                    """
                )
            )
        ),
     @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews")
	public ResponseEntity<HashMap<String, Object>> selectCrewList(
													@RequestParam(value="cpage", defaultValue="1") 
													int currentPage,
																							@RequestParam(value="sort", defaultValue="registered") String sort,
													@RequestHeader(value = "Authorization", required = false) String authHeader){
		// 로그인 여부 확인
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		// 한페이지당 몇개씩 보여질건지, 페이징바 숫자 갯수
		int boardLimit = 6;
		int pageLimit = 5;
		
		// PageRequest의 페이지 번호는 0부터 시작하므로 currentPage - 1
		Pageable pageable = PageRequest.of(currentPage -1, boardLimit);
		
		// DB에서 페이징된 크루 목록 조회 / 객체로 받아오기
		Page<Crew> page = crewService.selectCrewList(pageable, sort);
		
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
	
	// ******* 크루 상세 조회 ***********
	// 로그인 한 모든 사용자 조회 가능 
	@Operation(
	        summary = "크루 상세 조회",
	        description = "크루 ID를 이용하여 특정 크루의 상세 정보를 조회합니다."
	    )
	    @ApiResponses({
	        @ApiResponse(
	            responseCode = "200",
	            description = "크루 상세 조회 성공"
	        ),
	        @ApiResponse(
	            responseCode = "401",
	            description = "로그인이 필요합니다."
	        ),
	        @ApiResponse(
	            responseCode = "404",
	            description = "존재하지 않는 크루입니다."
	        )
	    })
	    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/{crewId}")
	public ResponseEntity<Crew> selectCrew(@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		
		Crew c = crewService.selectCrew(crewId);
		
		// 존재하지 않는 크루는 404 반환
		if(c == null) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(c);
		
	}
	
	// ************* 크루 검색 ****************
	//	로그인한 모든 사용자가 검색 가능 
	/*
	 * keyword : 검색어
     * cpage   : 현재 페이지
     * sort    : 정렬 기준
	 */
	@Operation(
	        summary = "크루 검색",
	        description = "검색어를 기준으로 크루를 검색하고 페이징하여 조회합니다."
	    )
	@ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 검색 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        )
    })
	@SecurityRequirement(name = "JWT")
	@GetMapping("crews/search")
	public ResponseEntity<HashMap<String, Object>> searchCrewList(
													@RequestParam(value="cpage", defaultValue="1")int currentPage, 
													@RequestParam String keyword,
													@RequestParam(value="sort", defaultValue="registered") String sort,
													@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		
		
		if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		
		// 	검색도 페이징 처리 될 수 있게 설정
		int boardLimit = 6;
		int pageLimit = 5;
		
		// > pageable 객체 생성
		Pageable pageable = PageRequest.of(currentPage-1, boardLimit);
		
		// pageable 을 넘기면서 조회
		Page<Crew> page = crewService.searchCrewList(keyword, pageable, sort);
		
		List<Crew> list = page.getContent();
		
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.
					getPageInfo((int)searchCount, currentPage, pageLimit, boardLimit);
		
		HashMap<String,Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list",list);
		
		return ResponseEntity.status(HttpStatus.OK).body(hm);
		
	}
	
	
	
	// ******** 크루 모집 글 등록 *********
	// EMPLOYEE만 가능 , 작성자는 JWT의 loginId를 이용하여 확인한다.
	@Operation(
        summary = "크루 모집 글 작성",
        description = "일반 직원이 크루 모집 글을 작성합니다. 작성자 정보는 JWT에서 가져옵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 작성 성공"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "입력값이 올바르지 않습니다."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없습니다."
        )
    })
	@SecurityRequirement(name = "JWT")
	@PostMapping("/crews")
	public ResponseEntity<String> insertCrew(@RequestBody Crew c,
										@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		 String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }
        
        
        if (!isEmployee(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }
        
        
        //[프론트에서 한 번 더 입력값 검증]
        // 필수 입력값 확인
		if (c == null || c.getCrewName() == null || 
			c.getCrewName().isBlank()) {
            return ResponseEntity.badRequest().body("fail");
        }
		
		
		
		// 작성일이 오늘보다 이전이면 등록하지 않음
		if (c.getCreatedDate() != null && 
			c.getCreatedDate().isBefore(LocalDate.now())) {
			return ResponseEntity
					.badRequest()
					.body("작성일은 오늘보다 이전일 수 없습니다.");
		}
		
		
		// XSS 공격 방지를 위해 입력값 필터링
		c.setCrewName(XssDefencePolicy.defence(c.getCrewName()));
		
		
		if (c.getCrewContent() != null) {
			c.setCrewContent(XssDefencePolicy.defence(c.getCrewContent()));
		}
		
		// JWT 에서 작성자의 loginId 추
        String loginId = authService.getLoginId(token);
        
        
        // 로그인한 직원의 loginId를 이용하여 크루 등
		Crew result = crewService.insertCrew(c, loginId);

        return ResponseEntity.ok(result != null ? "success" : "fail");
	}
	
	
	
	// ********** 크루 글 수정 ***********
	// 해당 Crew의 작성자만 가능
    @Operation(
            summary = "크루 모집 글 수정",
            description = "크루 작성자 또는 SUPER_ADMIN이 모집 글을 수정합니다."
        )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 수정 성공"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "수정 데이터가 올바르지 않습니다."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "수정 권한이 없습니다."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 크루입니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@PutMapping("/crews/{crewId}")
	public ResponseEntity<String> updateCrew(@PathVariable("crewId")int crewId, @RequestBody Crew c,
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

	    // 관리자도 아니고 해당 Crew 작성자도 아닌 경우 수정불가
	    if (!isSuperAdmin(token) && !isCrewLeader(token, existingCrew)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
	    }

	    // 수정 데이터가 없는 경우
		if (c == null || c.getCrewName() == null || 
				c.getCrewName().isBlank()) {
	        return ResponseEntity.badRequest().body("fail");
	    }
		
		
		// XSS 방어
		c.setCrewName(XssDefencePolicy.defence(c.getCrewName()));
		if (c.getCrewContent() != null) {
			c.setCrewContent(XssDefencePolicy.defence(c.getCrewContent()));
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
    
    
    // ******* 크루 글삭제 ********
	// 해당 Crew의 작성자만 가능
    @Operation(
        summary = "크루 모집 글 삭제",
        description = "크루 작성자 또는 SUPER_ADMIN이 크루 모집 글을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 삭제 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "삭제 권한이 없습니다."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 크루입니다."
        )
    })
    @SecurityRequirement(name = "JWT")
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
            		
            	
    // ******** 크루 가입 신청 *********
    /*
     * 일반 직원이 크루 가입을 신청한다.
     *
     * 직원의 loginId는 JWT에서 가져오기 때문에
     * 프론트에서 employeeId를 따로 전달할 필요가 없다.
     * 
     */
    @Operation(
        summary = "크루 가입 신청",
        description = "일반 직원이 특정 크루에 가입을 신청합니다. 직원 정보는 JWT에서 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 가입 신청 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없습니다."
        ),
        @ApiResponse(
            responseCode = "409",
            description = "가입할 수 없는 상태입니다. 이미 가입했거나 가입 조건을 만족하지 않습니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@PostMapping("/crews/{crewId}/join")
	public ResponseEntity<String> joinCrew(@PathVariable int crewId,
											@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
	    }

	    // 일반 직원만 신청 가능
	    if (!authService.isEmployeeToken(token)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
	    }
	    
	    // JWT에서 로그인 한 직원의 loginId 가져오기
	    String loginId = authService.getLoginId(token);
	    
	    // 가입 가능 여부 확인
	    String failureReason = crewService.getJoinFailureReason(crewId, loginId);
	    
	    
	    if (failureReason != null) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(failureReason);
	    }

	    // 크루 가입 처리
	    CrewMemberHist result = crewService.joinCrew(crewId, loginId);

	    return ResponseEntity.ok(result != null ? "success" : "fail");
		
	}
	
	
	
	
	

	// 내가 신청한 크루 조회
    /*
     * 로그인한 직원이 현재 가입한 크루 목록을 조회한다.
     *
     * employeeId를 URL로 받지 않고
     * JWT의 loginId를 사용한다.
     * 
     */
	// EMPLOYEE
    @Operation(
        summary = "내가 가입한 크루 조회",
        description = "JWT에 저장된 로그인 사용자가 가입한 크루 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "가입한 크루 목록 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없습니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/mylist")
	public ResponseEntity<List<Crew>> selectMyCrewList(
		@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 로그인한 직원의 loginId를 JWT에서 가져옴
        if (!isEmployee(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        
        String loginId = authService.getLoginId(token);
        
        /*
         * CrewMemberHist에서 Crew 객체만 추출.
         *
         * null인 Crew는 제외.
         * 
         */

		List<Crew> crews = crewService.selectMyCrewList(loginId).stream()
				.map(CrewMemberHist::getCrew)
				.filter(crew -> crew != null)
				.toList();

		return ResponseEntity.ok(crews);
	}
	
	
	
	// *********** 내가 만든 크루 조회 **********
    // 로그인한 직원이 크루장을 맡고 있는 크루 목록을 조회한다. 
    // 신청 마감, 모집마감  같은 것들 도 다 조회
    @Operation(
        summary = "내가 만든 크루 조회",
        description = "로그인한 직원이 크루장인 크루 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "내가 만든 크루 목록 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요하거나 JWT가 유효하지 않습니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없습니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/mycreated")
	public ResponseEntity<List<Crew>> selectMyCreatedCrewList(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
    	
    	String token = getToken(authHeader);
    	
    	
		if (token == null || !isEmployee(token)) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			
		}
		
		String loginId = authService.getLoginId(token);

        return ResponseEntity.ok(
            crewService.getLeaderCrews(loginId)
        );
	}
    
    

	//내가 모집 중인 활성 크루 목록 조회
    /*
     * 로그인한 직원이 만든 크루 중
     * 현재 모집 중인 활성 크루만 조회한다.
     * 
     */
    @Operation(
        summary = "내가 만든 활성 크루 조회",
        description = "로그인한 직원이 만든 크루 중 현재 활성 상태인 크루를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "활성 크루 목록 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요하거나 JWT가 유효하지 않습니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없습니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/mycreated/active")
	public ResponseEntity<List<Crew>> selectMyActiveCreatedCrewList(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
    	String token = getToken(authHeader);
    	
    	
		if (token == null || !authService.isEmployeeToken(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		

		return ResponseEntity.ok(crewService.selectActiveCreatedCrewList(authService.getLoginId(token)));

    }
    
    
    
    // ******* 크루의 페이지 위치 조회 **********
    /*
     * 특정 크루가 전체 활성 크루 목록에서
     * 몇 번째 페이지에 위치하는지 계산한다.
     *
     * 현재 한 페이지당 6개 크루를 표시하기 때문에
     * count / 6 + 1 방식으로 페이지를 계산한다.
     */
    
    @Operation(
        summary = "크루 페이지 위치 조회",
        description = "특정 크루가 활성 크루 목록에서 몇 번째 페이지에 위치하는지 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "페이지 위치 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/position/{crewId}")
	public ResponseEntity<Integer> selectCrewPage(@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (getToken(authHeader) == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		long crewsBefore = crewService.countActiveCrewsAfter(crewId);
		return ResponseEntity.ok((int) (crewsBefore / 6) + 1);
	}
	
	
	
	
	

	// ************ 크루 멤버 이름 조회 ************
    // 특정 크루에 가입한 멤버들의 이름 조회
    @Operation(
        summary = "크루 멤버 조회",
        description = "특정 크루에 가입한 멤버들의 이름을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 멤버 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@GetMapping("/crews/{crewId}/members")
	public ResponseEntity<ArrayList<String>> selectCrewMemberNames(
			@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		// String token = getToken(authHeader);
		
		
		if (!isAuthenticated(authHeader)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		return ResponseEntity.ok(crewService.selectCrewMemberNames(crewId));
		
		
	}
	
	
	
	
	
	

	// 크루 탈퇴
    
    /*
     * 일반 직원이 가입한 크루에서 탈퇴.
     *
     * 크루장은 자신이 만든 크루에서 탈퇴할 수 없다.
     */
    
    @Operation(
        summary = "크루 탈퇴",
        description = "가입한 크루에서 탈퇴합니다. 크루장은 자신이 만든 크루에서 탈퇴할 수 없습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "크루 탈퇴 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인이 필요합니다."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "직원 권한이 없거나 크루장이므로 탈퇴할 수 없습니다."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 크루입니다."
        )
    })
    @SecurityRequirement(name = "JWT")
	@DeleteMapping("/crews/{crewId}/join")
	public ResponseEntity<String> leaveCrew(@PathVariable int crewId,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		
		 String token = getToken(authHeader);

		    if (token == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
		    }

		    // 일반 직원만 탈퇴 가능
		    if (!isEmployee(token)) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
		    }

		    String loginId = authService.getLoginId(token);
		    
		    // 탈퇴하려는 크루 조
		    Crew existingCrew = crewService.selectCrew(crewId);

			if (existingCrew == null) {
				return ResponseEntity.notFound().build();
			}
			
			// 크루장은 자신의 크에서 탈퇴 할 수 없음 
			if (isCrewLeader(token, existingCrew)) {
				return ResponseEntity
						.status(HttpStatus.FORBIDDEN)
						.body("작성자는 크루에서 탈퇴할 수 없습니다.");
			}

		    int result = crewService.leaveCrew(crewId, loginId);

		    return ResponseEntity.ok(result > 0 ? "success" : "fail");
		
	}
	
	
	
	
	
//------------------------------------------------------------
	
	
	

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
