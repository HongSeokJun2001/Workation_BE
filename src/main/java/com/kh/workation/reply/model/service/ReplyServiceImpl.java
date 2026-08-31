package com.kh.workation.reply.model.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.workation.reply.model.dao.ReplyDao;
import com.kh.workation.reply.model.vo.Reply;

@Service
public class ReplyServiceImpl implements ReplyService{
	
	@Autowired
	private ReplyDao replyDao;

	@Override
	public ArrayList<Reply> selectReplyList(int crewId) {
		// TODO Auto-generated method stub
		//return replyDao.findByCrewIdContaningStatusOrderByCreatedDateDesc(crewId,"NORMAL");
		return null;
	}

	@Override
	public Reply insertReply() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteReply(int replyId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
