package com.kh.workation.reply.model.service;

import java.util.ArrayList;

import com.kh.workation.reply.model.vo.Reply;

public interface ReplyService{
	
	
	//댓글 조회
	ArrayList<Reply> selectReplyList(int crewId);
	
	
	// 댓글 작성
	Reply insertReply();
	
	
	//댓글 삭제
	int deleteReply(int replyId);

}
