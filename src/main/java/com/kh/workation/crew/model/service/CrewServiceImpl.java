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
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;

@Service
public class CrewServiceImpl implements CrewService{
	
	@Autowired
	private CrewDao crewDao;
	
	@Autowired
	private CrewMemberHistDao crewMemberHistDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	
	@Override
	public Page<Crew> selectCrewList(Pageable pageable, String sort) {
		if ("deadline".equals(sort)) {
			return crewDao.findByStatusOrderByEndDateAscCrewIdDesc("Y", pageable);
		}
		return crewDao.findByStatusOrderByCreatedDateDescCrewIdDesc("Y", pageable);
	}
	
	public Crew selectCrew(int crewId) {
		
		return crewDao.findById(crewId).orElse(null);
	}
	
	public Page<Crew> searchCrewList(String keyword, Pageable pageable, String sort){
		if ("deadline".equals(sort)) {
			return crewDao.findByCrewNameContainingAndStatusOrderByEndDateAscCrewIdDesc(keyword, "Y", pageable);
		}
		return crewDao.findByCrewNameContainingAndStatusOrderByCreatedDateDescCrewIdDesc(keyword, "Y", pageable);
	}
	
	
	@Transactional
	@Override
	public Crew insertCrew(Crew c, String loginId) {
		Employee employee = employeeDao
				.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElse(null);

		if (employee == null) {
			return null;
		}

		c.setEmployee(employee);
		Crew savedCrew = crewDao.save(c);
		joinCrew(savedCrew.getCrewId(), loginId);
		return savedCrew;
		
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
	


	
	//------------------------------------------------------

	
	
	@Override
	@Transactional
	public CrewMemberHist joinCrew(int crewId, String loginId) {
		
		// 이미 가입한 크루인지 확인
	    if (crewMemberHistDao.existsByEmployee_LoginIdAndCrew_CrewIdAndStatus(
	            loginId, crewId, "ACTIVE")) {
	        return null;
	    }

	    // JWT의 loginId로 현재 로그인한 직원 조회
	    Employee employee = employeeDao
	            .findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
	            .orElse(null);

	    // 가입하려는 크루 조회
	    Crew crew = crewDao.findById(crewId).orElse(null);

	    if (employee == null || crew == null) {
	        return null;
	    }

	    // 크루 가입 이력 생성
	    CrewMemberHist cm = new CrewMemberHist();
	    cm.setEmployee(employee);
	    cm.setCrew(crew);
	    cm.setStatus("ACTIVE");

	    return crewMemberHistDao.save(cm);
	}
	
	

	@Override
	public List<CrewMemberHist> selectMyCrewList(String loginId) {
		return crewMemberHistDao.findMyCrewList(loginId, "ACTIVE");
	}

	@Override
	public ArrayList<String> selectCrewMemberNames(int crewId) {
		return crewMemberHistDao.findActiveEmployeeNamesByCrewId(crewId);
	}
	
	
	

	@Override
	@Transactional
	public int leaveCrew(int crewId, String loginId) {
	    return crewMemberHistDao.leaveCrew(
	            loginId,
	            crewId,
	            LocalDateTime.now()
	    );
	}




	
	
	//--------------------------------------------------------
	
	
	
	
	@Override
	@Transactional(readOnly = true)
	public List<Crew> getLeaderCrews(String loginId) {
		
		return crewDao.findByEmployeeLoginId(loginId);
	}

}
