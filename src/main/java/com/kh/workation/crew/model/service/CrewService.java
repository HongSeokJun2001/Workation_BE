package com.kh.workation.crew.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.reply.model.vo.Reply;

public interface CrewService {
	
	// 크루 리스트 조
	ArrayList<Crew> selectCrewList();
	
	// 크루 등록
	Crew inserCrew(Crew c);
	
	// 크루 수정
	
	Crew updateCrew(Crew c);
	
	// 크루삭제
	int deleteCrew(int CrewId);
	
	// 크루 검색
	Page<Crew> selectSearchList(String keyword, Pageable pageable);
	
	// 댓글 조회 
	List<Reply> selectReplyList(int crewId);
	
	// 댓글 등록 
	Reply insertReply(Reply r);
	
	// 댓글 삭제 
	int delteReply(int replyId);
	
	
	

}
