package com.kh.workation.reply.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.kh.workation.reply.model.service.ReplyService;
import com.kh.workation.reply.model.vo.Reply;

public class ReplyController {
	
	@Autowired
	private ReplyService replyService;
	

	
	// 댓글 조회
//		@GetMapping("/crews/reply/{crewId}")
//		public ResponseEntity<ArrayList<Reply>> selectReplyList(@PathVariable("crewId")int crewId){
//			
//			ArrayList<Reply> list = replyService.selectReplyList(crewId);
//				
//			return ResponseEntity.status(HttpStatus.OK).body(list);
//		
//			
//		}
}
