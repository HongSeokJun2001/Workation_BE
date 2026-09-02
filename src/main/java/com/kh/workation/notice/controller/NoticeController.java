package com.kh.workation.notice.controller;

import java.time.LocalDateTime;
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
import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.notice.model.service.NoticeService;
import com.kh.workation.notice.model.vo.Notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
// @RequestMapping("/notices") 
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;

	@Autowired
	private AuthService authService;
	
	// JWT 인증 공동 처리 
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

    // 모든 인증된 사용자 조회 가능
    private boolean hasAnyAuthUser(String token) {
        return token != null && authService.isValidToken(token);
    }
    
    // 401 = 너 누구야?
    // 403 = 너인 건 알겠는데 이 기능은 못 써.
	
    
	@Operation(summary="공지사항 전체목록 조회(페이징)", description="페이지 번호(cpage)에 해당하는 공지사항 목록을 조회합니다."
													+ "응답 : {list : 공지사항목록, pi :페이지 정보}")
	@ApiResponse(responseCode="200", description="조회성공",
				content=@Content(mediaType="application/json",
								examples=@ExampleObject(value="""
										{
											"list" : [{},{},{}],
											"pi" : {
												"listCount" : 42,
												"currentPage" : 1,
												"pageLimit" : 5,
												"boardLimit" : 5,
												"maxPage" : 9,
												"startPage" : 1,
												"endPage" : 5,
											}
										
										}
										
										""")))
	@GetMapping("/notices")
	public ResponseEntity<HashMap<String,Object>> selectNoticeList(
			@RequestParam(value="cpage", defaultValue="1")int currentPage,
			@RequestHeader(value = "Authorization", required = false) String authHeader){
		// JWT가 없거나 유효하지 않으면 조회 불가
		String token = getToken(authHeader);

        if (!hasAnyAuthUser(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        
		// 사용자가 요청한 currentPage 는 매개변수로 받아온 상태!!
				// + boardLimit(한 페이지당 몇개씩 보여질건지), pageLimit(페이징바 숫자 갯수) 만 마저 셋팅
				int boardLimit = 5;
				int pageLimit = 5;
				
				// > 위의 값들을 가지고 Pageable 객체를 먼저 셋팅
				Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
				
				// > 위에서 셋팅한 Pageable 객체를 넘기면서 실제 목록을 조회해오기 (구간별로)
				//   이 때, 조회된 결과는 Page 객체로 받아온다!!
				Page<Notice> page = noticeService.selectNoticeList(pageable);
				
				// > Page 객체로부터 조회된 리스트, 총 게시글의 갯수 구해보기
				List<Notice> list = page.getContent();
				
				long listCount = page.getTotalElements();
				// > 내부적으로 COUNT 함수를 실행해서 갯수를 세오는 것!!
				
				// > PageInfo 객체 생성하기
				PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, 
															pageLimit, boardLimit);
				
				HashMap<String, Object> hm = new HashMap<>();
				
				hm.put("list", list); // 실제 목록 (tbody) 에 출력할 용도
				hm.put("pi", pi); // 페이징바 만들어낼 용도
				
				return ResponseEntity.status(HttpStatus.OK)
									 .body(hm);
					
	}
	
	@Operation(summary="공지사항 작성", description="공시자항을 작성합니다. JWT토큰에 작성자정보를 추출하므로 로그인이 필요합니다.")
	@ApiResponse(responseCode="200", description="body 로 success/fail 응답")
	@SecurityRequirement(name="JWT")
	@PostMapping("/notices")
	public ResponseEntity<String> insertNotice(@RequestBody Notice n,
										@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

        // 로그인하지 않았거나 JWT가 유효하지 않은 경우
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        // SUPER_ADMIN이 아닌 경우
        if (!isSuperAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        // 요청 데이터 확인
        if (n == null ||
            n.getNoticeTitle() == null || n.getNoticeTitle().isBlank() ||
            n.getNoticeContent() == null || n.getNoticeContent().isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

        // 기본값 설정
        if (n.getStatus() == null || n.getStatus().isBlank()) {
            n.setStatus("Y");
        }

        if (n.getCreateDate() == null) {
            n.setCreateDate(LocalDateTime.now());
        }

        if (n.getUpdateDate() == null) {
            n.setUpdateDate(LocalDateTime.now());
        }

        // 하드코딩된 관리자 ID
        // TODO: JWT의 loginId로 실제 Admin을 조회해서 넣어야 함
        /*
        Admin tempAdmin = new Admin();
        tempAdmin.setAdminId(1L);
        n.setAdmin(tempAdmin);
        */

        Notice result = noticeService.insertNotice(n);

        return ResponseEntity.ok(result != null ? "success" : "fail");
	}
	
	
	//공지사항 상세 조회용 컨트롤러
	// 모든 로그인 사용자가 조회 가능 
	@GetMapping("/notices/{noticeId}")
	public ResponseEntity<Notice> selectNotice(@PathVariable("noticeId") int noticeId,
			@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

        if (!hasAnyAuthUser(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
		int result = noticeService.increaseCount(noticeId);
		
		if(result > 0) {
			
			Notice n = noticeService.selectNotice(noticeId);
			
			return ResponseEntity.status(HttpStatus.OK).body(n);
			
		}else {
			
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}
		
		
	}
	
	
	// 공지사항 수정용 컨트롤러
	// SUPER_ADMIN 만 가능
	@PutMapping("/notices/{noticeId}")
	public ResponseEntity<String> updateNotice(@PathVariable("noticeId") int noticeId,
											@RequestBody Notice n,
											@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        if (!isSuperAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        if (n == null ||
            n.getNoticeTitle() == null || n.getNoticeTitle().isBlank() ||
            n.getNoticeContent() == null || n.getNoticeContent().isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

        n.setNoticeId(noticeId);

        if (n.getStatus() == null || n.getStatus().isBlank()) {
            n.setStatus("Y");
        }

        n.setUpdateDate(LocalDateTime.now());

        // 하드코딩된 관리자 ID
        // TODO: JWT의 loginId로 실제 Admin을 조회해서 넣어야 함
        /*
        if (n.getAdmin() == null) {
            Admin tempAdmin = new Admin();
            tempAdmin.setAdminId(1L);
            n.setAdmin(tempAdmin);
        }
        */

        Notice result = noticeService.updateNotice(n);

        return ResponseEntity.ok(result != null ? "success" : "fail");
	}
	
	//공지사항 삭제용 컨트롤러
	// SUPER_ADMIN만 가능
	@DeleteMapping("/notices/{noticeId}")
	public ResponseEntity<String> deleteNotice(@PathVariable("noticeId") int noticeId,
										@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		
		String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        if (!isSuperAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }
		
		int result = noticeService.deleteNotice(noticeId);
		String message = (result > 0) ? "success" : "fail";
		return ResponseEntity.status(HttpStatus.OK).body(message);
		
		
	}
	
	

	

}
