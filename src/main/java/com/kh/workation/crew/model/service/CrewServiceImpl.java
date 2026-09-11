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
import com.kh.workation.member.model.dao.CompanyDao;
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

	@Autowired
	private CompanyDao companyDao;
	
	
	@Override
	public Page<Crew> selectCrewList(Pageable pageable, String sort) {
		if ("endDate".equals(sort) || "deadline".equals(sort)) {
			return crewDao.findByStatusOrderByEndDateAscCrewIdDesc("Y", pageable);
		}
		return crewDao.findByStatusOrderByCrewIdDesc("Y", pageable);
	}
	
	public Crew selectCrew(int crewId) {
		
		return crewDao.findById(crewId).orElse(null);
	}
	
	public Page<Crew> searchCrewList(String keyword, Pageable pageable, String sort){
		if ("endDate".equals(sort) || "deadline".equals(sort)) {
			return crewDao.searchByCrewNameOrCompanyNameOrderByEndDateAscCrewIdDesc(keyword, "Y", pageable);
		}
		return crewDao.searchByCrewNameOrCompanyNameOrderByCrewIdDesc(keyword, "Y", pageable);
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
		Integer availableDays = employee.getWorkationAvailDays();
		if (c.getWorkUsedDays() == null || c.getWorkUsedDays() < 1
				|| availableDays == null || c.getWorkUsedDays() > availableDays) {
			return null;
		}
		c.setCreatedDate(java.time.LocalDate.now());

		c.setEmployee(employee);
		c.setCompany(companyDao.getReferenceById(employee.getCompanyId()));
		Crew savedCrew = crewDao.save(c);

		joinCrew(savedCrew.getCrewId(), loginId);

		return savedCrew;
		
	}
	
	@Transactional
	@Override
	public Crew updateCrew(Crew c) {
		Crew existingCrew = crewDao.findById(c.getCrewId()).orElse(null);
		if (existingCrew == null) {
			return null;
		}
		int availableDays = existingCrew.getEmployee() != null
				&& existingCrew.getEmployee().getWorkationAvailDays() != null
				? existingCrew.getEmployee().getWorkationAvailDays() : 0;
		if (c.getWorkUsedDays() == null || c.getWorkUsedDays() < 1
				|| c.getWorkUsedDays() > availableDays) {
			return null;
		}

		existingCrew.setCrewName(c.getCrewName());
		existingCrew.setCrewContent(c.getCrewContent());
		existingCrew.setStatus(c.getStatus());
		// 작성 시각은 수정할 수 없도록 기존 값을 유지합니다.
		existingCrew.setEndDate(c.getEndDate());
		existingCrew.setCapacity(c.getCapacity());
		if (c.getWorkUsedDays() != null) {
			existingCrew.setWorkUsedDays(c.getWorkUsedDays());
		}

		return crewDao.save(existingCrew);
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
		if (getJoinFailureReason(crewId, loginId) != null) {
			return null;
		}

		Employee employee = employeeDao
				.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElse(null);
		Crew crew = crewDao.findById(crewId).orElse(null);

		CrewMemberHist cm = new CrewMemberHist();
		cm.setEmployee(employee);
		cm.setCrew(crew);
		cm.setStatus("ACTIVE");

		return crewMemberHistDao.save(cm);
	}

	@Override
	@Transactional(readOnly = true)
	public String getJoinFailureReason(int crewId, String loginId) {
		
		// 이미 가입한 크루인지 확인
	    if (crewMemberHistDao.existsByEmployee_LoginIdAndCrew_CrewIdAndStatus(
	            loginId, crewId, "ACTIVE")) {
	        return "이미 가입한 크루입니다.";
	    }

	    // JWT의 loginId로 현재 로그인한 직원 조회
	    Employee employee = employeeDao
	            .findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
	            .orElse(null);

	    // 가입하려는 크루 조회
	    Crew crew = crewDao.findById(crewId).orElse(null);

	    if (employee == null || crew == null) {
	        return "직원 또는 크루 정보를 찾을 수 없습니다.";
	    }

		Long crewCompanyId = crew.getCompany() == null ? null : crew.getCompany().getCompanyId();
		if (crewCompanyId == null || !crewCompanyId.equals(employee.getCompanyId())) {
			return "같은 회사 직원만 크루 신청이 가능합니다.";
		}

	    if (crew.getEndDate() != null && crew.getEndDate().isBefore(LocalDateTime.now().toLocalDate())) {
	        return "모집 기간이 종료되었습니다.";
	    }

boolean isCreator = crew.getEmployee() != null
            && loginId.equals(crew.getEmployee().getLoginId());

    List<CrewMemberHist> activeMembers = crewMemberHistDao
			.findByCrewCrewIdAndStatusWithEmployee(crewId, "ACTIVE");
    long memberCount = activeMembers.stream()
			.map(CrewMemberHist::getEmployee)
			.filter(member -> member != null)
			.map(Employee::getEmployeeId)
			.distinct()
			.count();
    boolean ownerAlreadyIncluded = crew.getEmployee() != null && activeMembers.stream()
			.anyMatch(member -> member.getEmployee() != null
					&& crew.getEmployee().getEmployeeId().equals(member.getEmployee().getEmployeeId()));
    if (crew.getEmployee() != null && !ownerAlreadyIncluded) {
		memberCount++;
    }
    if (!isCreator && crew.getCapacity() != null && memberCount >= crew.getCapacity()) {
        return "모집 정원이 마감되었습니다.";
	    }

	    int requiredDays = crew.getWorkUsedDays() == null ? 1 : crew.getWorkUsedDays();
	    int availableDays = employee.getWorkationAvailDays() == null ? 0 : employee.getWorkationAvailDays();
	    if (availableDays < requiredDays) {
	        return "워케이션 가용일수가 부족합니다.";
	    }

	    return null;
	}
	
	

	@Override
	public List<CrewMemberHist> selectMyCrewList(String loginId) {
		return crewMemberHistDao.findMyCrewList(loginId, "ACTIVE");
	}

	@Override
	@Transactional(readOnly = true)
	public ArrayList<String> selectCrewMemberNames(int crewId) {
		ArrayList<String> names = crewMemberHistDao.findActiveEmployeeNamesByCrewId(crewId);
		Crew crew = crewDao.findById(crewId).orElse(null);
		if (crew != null && crew.getEmployee() != null
				&& !names.contains(crew.getEmployee().getEmployeeName())) {
			names.add(0, crew.getEmployee().getEmployeeName());
		}
		return names;
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




	@Override
	@Transactional(readOnly = true)
	public List<Crew> selectActiveCreatedCrewList(String loginId) {
		return crewDao.findByEmployeeLoginIdAndStatusOrderByCrewIdDesc(loginId, "Y");
	}
	
	//--------------------------------------------------------
	
	
	
	
	

	@Override
	@Transactional(readOnly = true)
	public List<Crew> getLeaderCrews(String loginId) {
		
		return crewDao.findFullCrewsByLeaderLoginId(loginId);
		
	}

	@Override
	@Transactional(readOnly = true)
	public long countActiveCrewsAfter(int crewId) {
		return crewDao.countByStatusAndCrewIdGreaterThan("Y", crewId);
	}

}
