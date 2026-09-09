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

	@Query("""
		SELECT COUNT(DISTINCT c.crewId)
		FROM Crew c
		LEFT JOIN c.crewMemberHists h
		WHERE c.status = 'Y'
		AND (
			c.employee.loginId = :loginId
			OR (h.employee.loginId = :loginId AND h.status = 'ACTIVE')
		)
		""")
	long countDistinctParticipatingCrews(@Param("loginId") String loginId);

	long countByStatusAndCrewIdGreaterThan(String status, Integer crewId);

	Page<Crew> findByStatusOrderByCrewIdDesc(String status, Pageable pageable);
	Page<Crew> findByStatusOrderByEndDateAscCrewIdDesc(String status, Pageable pageable);


	Page<Crew> findByCrewNameContainingAndStatusOrderByCrewIdDesc(String keyword, String status, Pageable pageable);
	Page<Crew> findByCrewNameContainingAndStatusOrderByEndDateAscCrewIdDesc(String keyword, String status, Pageable pageable);


	@Modifying
	@Query("""
			
			UPDATE Crew c
			SET c.status = 'N'
			WHERE c.crewId = :crewId
			AND c.status = 'Y'
			
			""")
	int deleteCrew(@Param("crewId")int crewId);
	
	List<Crew> findByEmployeeLoginIdAndStatusOrderByCrewIdDesc(String loginId, String status);
         
	@Query("""
		    SELECT c
		    FROM Crew c
		    LEFT JOIN c.crewMemberHists h
		        ON h.status = 'ACTIVE'
		    WHERE c.employee.loginId = :loginId
		    GROUP BY c
		    HAVING COUNT(DISTINCT h) = c.capacity
		    """)
		List<Crew> findFullCrewsByLeaderLoginId(
		    @Param("loginId") String loginId
		);

}

