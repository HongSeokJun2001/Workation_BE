package com.kh.workation.reply.model.service;

import java.util.List;

import com.kh.workation.reply.model.vo.Reply;

public interface ReplyService{
	
	
	//댓글 조회
	List<Reply> selectReplyList(int crewId);
	
	
	// 댓글 작성
	Reply insertReply(Reply r, int crewId, String loginId);
	
	
	//댓글 삭제
	int deleteReply(int replyId, String loginId);
	
	//대댓글 

}
