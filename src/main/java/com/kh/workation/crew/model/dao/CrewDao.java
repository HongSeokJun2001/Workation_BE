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
         
         
	// 특정 크루보다 뒤에 등록된 활성 크루 수 조회, 크루 상세 위치 계산에 사용
	long countByStatusAndCrewIdGreaterThan(String status, Integer crewId);

	//활성 크루를 크루 번호 내림차 순으로 페이징 조회
	Page<Crew> findByStatusOrderByCrewIdDesc(String status, Pageable pageable);
	
	//활성 크루를 모집 마감일 오름차 순으로 페이징 조회
	Page<Crew> findByStatusOrderByEndDateAscCrewIdDesc(String status, Pageable pageable);


	//크루 이름과 상태가 일치하는 크루를 크루 번호 내림차순으로 검색
	Page<Crew> findByCrewNameContainingAndStatusOrderByCrewIdDesc(String keyword, String status, Pageable pageable);
	
	// 크루 이름 또는 회사명으로 활성 크루를 크루 번호 내림차순으로 검색
	@Query("""
			SELECT c
			FROM Crew c
			LEFT JOIN c.company comp
			WHERE c.status = :status
			  AND (
			      c.crewName LIKE %:keyword%
			      OR comp.companyName LIKE %:keyword%
			  )
			ORDER BY c.crewId DESC
		""")
	Page<Crew> searchByCrewNameOrCompanyNameOrderByCrewIdDesc(@Param("keyword") String keyword,
																     @Param("status") String status,
																     Pageable pageable);
	
	// 크루 이름 또는 회사명으로 활성 크루를 모집 마감일 오름차순으로 검색
	@Query("""
			SELECT c
			FROM Crew c
			LEFT JOIN c.company comp
			WHERE c.status = :status
			  AND (
			      c.crewName LIKE %:keyword%
			      OR comp.companyName LIKE %:keyword%
			  )
			ORDER BY c.endDate ASC, c.crewId DESC
		""")
	Page<Crew> searchByCrewNameOrCompanyNameOrderByEndDateAscCrewIdDesc(@Param("keyword") String keyword,
																     @Param("status") String status,
																     Pageable pageable);

	// 크루 모집글을 삭제하지 않고 상태를 비활성('N')으로 변경하는 소프트 삭제
	@Modifying
	@Query("""
			
			UPDATE Crew c
			SET c.status = 'N'
			WHERE c.crewId = :crewId
			AND c.status = 'Y'
			
			""")
	int deleteCrew(@Param("crewId")int crewId);
	
    
	



	// 특정 직원이 작성한 활성 모집글 조회
	List<Crew> findByEmployeeLoginIdAndStatusOrderByCrewIdDesc(String loginId, String status);
         
         
	// 특정 직원이 작성한 정원이 마감된 활성 모집글 조회         
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

