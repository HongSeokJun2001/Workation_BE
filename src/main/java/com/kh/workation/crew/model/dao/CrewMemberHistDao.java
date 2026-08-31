package com.kh.workation.crew.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.crew.model.vo.CrewMemberHist;

public interface CrewMemberHistDao extends JpaRepository<CrewMemberHist, Integer>{

	List<CrewMemberHist> findByEmployee_EmployeeIdAndStatus(Long employeeId, String status);

	boolean existsByEmployee_EmployeeIdAndCrew_CrewIdAndStatus(
			Long employeeId, Integer crewId, String status);

	@Modifying
	@Query("""
			UPDATE CrewMemberHist h
			SET h.status = 'LEFT', h.leftDate = :leftDate
			WHERE h.employee.employeeId = :employeeId
			AND h.crew.crewId = :crewId
			AND h.status = 'ACTIVE'
			""")
	int leaveCrew(@Param("employeeId") Long employeeId,
			@Param("crewId") Integer crewId,
			@Param("leftDate") LocalDateTime leftDate);
	
}
