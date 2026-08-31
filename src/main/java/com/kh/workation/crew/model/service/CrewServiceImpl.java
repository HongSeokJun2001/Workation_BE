package com.kh.workation.crew.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.crew.model.dao.CrewMemberHistDao;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.reply.model.vo.Reply;

@Service
public class CrewServiceImpl implements CrewService{
	
	@Autowired
	private CrewDao crewDao;
	
	@Autowired
	private CrewMemberHistDao crewMemberHistDao;
	
	
	@Override
	public Page<Crew> selectCrewList(Pageable pageable) {
		
		return crewDao.findByStatusOrderByCrewIdDesc("Y", pageable);
	}
	
	public Crew selectCrew(int crewId) {
		
		return crewDao.findById(crewId).orElse(null);
	}
	
	public Page<Crew> searchCrewList(String keyword, Pageable pageable){
		return crewDao.findByCrewNameContainingAndStatusOrderByCrewIdDesc(keyword,"Y", pageable);
	}
	
	
	@Transactional
	@Override
	public Crew insertCrew(Crew c) {
		
		return crewDao.save(c);
		
	}
	
	@Transactional
	@Override
	public Crew updateCrew(Crew c) {
		// TODO Auto-generated method stub
		return crewDao.save(c);
	}

	@Override
	@Transactional
	public int deleteCrew(int crewId) {
		return crewDao.deleteCrew(crewId);
	}

	@Override
	@Transactional
	public CrewMemberHist joinCrew(CrewMemberHist cm) {
		if (crewMemberHistDao.existsByEmployee_EmployeeIdAndCrew_CrewIdAndStatus(
				cm.getEmployee().getEmployeeId(), cm.getCrew().getCrewId(), "ACTIVE")) {
			return null;
		}
		cm.setStatus("ACTIVE");
		return crewMemberHistDao.save(cm);
	}

	@Override
	public List<CrewMemberHist> selectMyCrewList(Long employeeId) {
		return crewMemberHistDao.findByEmployee_EmployeeIdAndStatus(employeeId, "ACTIVE");
	}

	@Override
	@Transactional
	public int leaveCrew(Long employeeId, int crewId) {
		return crewMemberHistDao.leaveCrew(employeeId, crewId, LocalDateTime.now());
	}

	@Override
	public int deleteReply(int replyId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<Crew> getLeaderCrews(String loginId) {
		
		return crewDao.findByEmployeeLoginId(loginId);
	}

}
