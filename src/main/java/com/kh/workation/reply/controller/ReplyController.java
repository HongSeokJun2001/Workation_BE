package com.kh.workation.reply.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.reply.model.service.ReplyService;
import com.kh.workation.reply.model.vo.Reply;

@CrossOrigin
@RestController
public class ReplyController {
	
	/*
	 * 조회 GET    /crews/{crewId}/replies
		작성 POST   /crews/{crewId}/replies
		수정 PUT    /replies/{replyId}
		삭제 DELETE /replies/{replyId}
	 */
	
	@Autowired
	private ReplyService replyService;
	
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
	

	
	 //댓글 조회
	@GetMapping("/crews/{crewId}/replies")
	public ResponseEntity<ArrayList<Reply>> selectReplyList(@PathVariable("crewId")int crewId, 
			@RequestHeader(value = "Authorization", required = false) String authHeader){
		
		 String token = getToken(authHeader);
		 
		// 로그인 여부 확인
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
		ArrayList<Reply> list = (ArrayList) replyService.selectReplyList(crewId);
			
		return ResponseEntity.status(HttpStatus.OK).body(list);
		
			
		}
	
	// 댓글 작성
	@PostMapping("/crews/{crewId}/replies")
	public ResponseEntity<String> insertReply(@PathVariable("crewId") int crewId, 
			@RequestBody Reply r,
			@RequestHeader(value = "Authorization", required = false) String authHeader){
			String token = getToken(authHeader);
			if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
		
		String loginId = authService.getLoginId(token);
		
			Reply insertR = replyService.insertReply(r, crewId, loginId);
		
		String message = (insertR != null)? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
	// 댓글 삭제
	@DeleteMapping("/crews/replies/{replyId}")
	public ResponseEntity<String> deleteReply(@PathVariable("replyId")int replyId,
			@RequestHeader(value = "Authorization", required = false)String authHeader){
		
		String token = getToken(authHeader);

	    // JWT가 없는 경우
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
	    }

	    String loginId = authService.getLoginId(token);

	    // 기존 Reply 조회
	    // Crew existingCrew = replyService.selectReply(replyId);

	    // 존재하지 않는 Crew
//	    if (existingCrew == null) {
//	        return ResponseEntity.notFound().build();
//	    }


	    // 삭제
	    int result = replyService.deleteReply(replyId, loginId);
	    
        return ResponseEntity.ok(result > 0 ? "success" : "fail");
		
		
	}
	
	
	
}
