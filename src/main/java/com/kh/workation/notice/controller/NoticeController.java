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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.notice.model.service.NoticeService;
import com.kh.workation.notice.model.vo.Notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
// @RequestMapping("/notices") 
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;
	
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
			@RequestParam(value="cpage", defaultValue="1")int currentPage){
		
//		ArrayList<Notice> list = (ArrayList)noticeService.selectNoticeList();
		
		// 사용자가 요청한 currentPage 는 매개변수로 받아온 상태!!
				// + boardLimit(한 페이지당 몇개씩 보여질건지), pageLimit(페이징바 숫자 갯수) 만 마저 셋팅
				int boardLimit = 5;
				int pageLimit = 5;
				
				// > 위의 값들을 가지고 Pageable 객체를 먼저 셋팅
				Pageable pageable = PageRequest.of(currentPage - 1, 5);
				
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
			
		
		//return ResponseEntity.status(HttpStatus.OK).body(list);
		
	}
	
//	@Operation(summary="공지사항 작성", description="공시자항을 작성합니다. JWT토큰에 작성자정보를 추출하므로 로그인이 필요합니다.")
//	@ApiResponse(responseCode="200", description="body 로 ssucess/fail 응답")
//	@SecurityRequirement(name="JWT")
	@PostMapping("/notices")
	public ResponseEntity<String> insertNotice(@RequestBody Notice n, HttpServletRequest request){
		
//		// System.out.println(n);
//				// > 작성자의 아이디가 안들어가있음!!
//				
//				// request 객체를 매개변수로 추가했고, 여기서부터 현재 로그인한 회원의 정보를 알아내기!!
//				// URL 요청의 Header 에 Authorization 으로 "Bearer xxxxx.xxxxxxx.xxxx" 를 넣어뒀음
//				// > 기존 인터셉터의 검증용 코드와 동일
//				String authHeader = request.getHeader("Authorization");
//				
//				// > 인터셉터에서 이미 검증을 거쳐왔기 때문에 중첩 if문 사용 X
//				//   그냥 바로 뽑아서 쓰면 된다!!
//				String jwtTokenString = authHeader.substring(7);
//				// > "xxxxx.xxxxxxx.xxxx"
//				
//				// 위의 jwtTokenString 을 파싱하기
//				Key key = Keys.hmacShaKeyFor(
//								AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//				
//				Claims claims = Jwts.parserBuilder()
//									.setSigningKey(key)
//									.build()
//									.parseClaimsJws(jwtTokenString)
//									.getBody();
//				
//				String userId = claims.getSubject();
//				
//				// System.out.println(userId);
//				
//				// 이 아이디값을 가지고 MemberService 를 호출해서 해당 회원의 정보를 Member 객체로 받아와야함
//				Member m = memberService.selectMember(userId);
//				
//				// System.out.println(m);
//				
//				// n 의 member 필드에 m 을 셋팅 (setter)
//				n.setMember(m);
//				
//				// 현재시간이 담겨있는 컬럼에 not null 제약조건이 걸려있다면?
//				n.setCreateDate(LocalDateTime.now());
						
				// XSS 공격 방지 처리 - 공통 코드 작업 참고
				//admin객체 우선 주입 
				Admin tempAdmin = new Admin();
			    tempAdmin.setAdminId(1L);
			    n.setAdmin(tempAdmin);
				
				Notice insertNo = noticeService.insertNotice(n);
				
				String message = (insertNo != null) ? "success" : "fail";
				
				return ResponseEntity.status(HttpStatus.OK)
									 .body(message);
		
		
//				System.out.println("========== Notice ==========");
//			    System.out.println(n);
//			    System.out.println("title = " + n.getNoticeTitle());
//			    System.out.println("content = " + n.getNoticeContent());
//			    System.out.println("status = " + n.getStatus());
//			    System.out.println("============================");
//		
//			    return ResponseEntity.ok("success");
			
			}
	
	//공지사항 상세 조회용 컨트롤러
	@GetMapping("/notices/{noticeId}")
	public ResponseEntity<Notice> selectNotice(@PathVariable("noticeId") int noticeId){
		
		int result = noticeService.increaseCount(noticeId);
		
		if(result > 0) {
			Notice n = noticeService.selectNotice(noticeId);
			
			return ResponseEntity.status(HttpStatus.OK).body(n);
			
		}else {
			
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}
		
		
	}
	
	
	//공지사항 수정용 컨트럴
	@PutMapping("/notices/{noticeId}")
	public ResponseEntity<String> updateNotice(@PathVariable("noticeId") int noticeId,
											@RequestBody Notice n){
		if (n == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
		}
		n.setNoticeId(noticeId);
		if (n.getStatus() == null || n.getStatus().isBlank()) {
			n.setStatus("Y");
		}
		if (n.getUpdateDate() == null) {
			n.setUpdateDate(LocalDateTime.now());
		}
		if (n.getAdmin() == null) {
			Admin tempAdmin = new Admin();
			tempAdmin.setAdminId(1L);
			n.setAdmin(tempAdmin);
		}
		if (n.getNoticeTitle() == null || n.getNoticeTitle().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
		}
		if (n.getNoticeContent() == null || n.getNoticeContent().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
		}
		
		Notice updateNo = noticeService.updateNotice(n);
		String message = (updateNo != null)? "success" : "fail";
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
	//공지사항 삭제용 컨트롤러
	@DeleteMapping("/notices/{noticeId}")
	public ResponseEntity<String> deleteNotice(@PathVariable("noticeId") int noticeId){
		
		int result = noticeService.deleteNotice(noticeId);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
	

	

}
