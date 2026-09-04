package com.kh.workation.crew.model.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.crew.model.vo.Crew;

public interface CrewDao extends JpaRepository<Crew, Integer>{

	Page<Crew> findByStatusOrderByCreatedDateDescCrewIdDesc(String status, Pageable pageable);
	Page<Crew> findByStatusOrderByEndDateAscCrewIdDesc(String status, Pageable pageable);


	Page<Crew> findByCrewNameContainingAndStatusOrderByCreatedDateDescCrewIdDesc(String keyword, String status, Pageable pageable);
	Page<Crew> findByCrewNameContainingAndStatusOrderByEndDateAscCrewIdDesc(String keyword, String status, Pageable pageable);


	@Modifying
	@Query("""
			
			UPDATE Crew c
			SET c.status = 'N'
			WHERE c.crewId = :crewId
			AND c.status = 'Y'
			
			""")
	int deleteCrew(@Param("crewId")int crewId);
	
	
	
	@Query("SELECT c FROM Crew c " +
	           "WHERE c.employee.loginId = :loginId " +
	           "AND c.crewId NOT IN (" +
	           "    SELECT a.crew.crewId FROM Application a " +
	           "    JOIN a.progress p " +
	           "    WHERE p.status IN ('APPLY', 'CONFIRM')" +
	           ")")
	List<Crew> findByEmployeeLoginId(@Param("loginId") String loginId);

}

