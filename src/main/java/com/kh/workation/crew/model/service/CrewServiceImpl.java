package com.kh.workation.crew.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.reply.model.vo.Reply;

@Service
public class CrewServiceImpl implements CrewService{
	
	@Autowired
	private CrewDao crewDao;
	
	@Override
	public Page<Crew> selectCrewList(Pageable pageable) {
		
		return crewDao.findByStatusOrderByCrewIdDesc("Y", pageable);
	}
	
	public Page<Crew> searchCrewList(String keyword, Pageable pageable){
		return crewDao.findByCrewNameContainingAndStatusOrderByCrewIdDesc(keyword,"Y", pageable);
	}
	
	@Transactional
	@Override
	public Crew insertCrew(Crew c) {
		
		return crewDao.save(c);
		
	}

	@Override
	public Crew updateCrew(Crew c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteCrew(int CrewId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<Crew> selectSearchList(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Reply> selectReplyList(int crewId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reply insertReply(Reply r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delteReply(int replyId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
