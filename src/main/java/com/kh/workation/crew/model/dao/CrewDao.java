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

	Page<Crew> findByStatusOrderByCrewIdDesc(String status, Pageable pageable);


	Page<Crew> findByCrewNameContainingAndStatusOrderByCrewIdDesc(String keyword, String string, Pageable pageable);


	@Modifying
	@Query("""
			
			UPDATE Crew c
			SET c.status = 'N'
			WHERE c.crewId = :crewId
			AND c.status = 'Y'
			
			""")
	int deleteCrew(@Param("crewId")int crewId);
	
	List<Crew> findByEmployeeLoginId(String loginId);

}

