package com.kh.workation.reply.model.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.reply.model.vo.Reply;

public interface ReplyDao extends JpaRepository<Reply, Integer>{

	//ArrayList<Reply> findByCrewIdContaningStatusOrderByCreatedDateDesc(int crewId, String string);

	
	
}
