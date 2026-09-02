package com.kh.workation.crew.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.crew.model.vo.CrewMemberHist;

public interface CrewMemberHistDao extends JpaRepository<CrewMemberHist, Integer>{


    // 크루 신청 중복 확인
	@Query("""
		    SELECT COUNT(h) > 0
		    FROM CrewMemberHist h
		    WHERE h.employee.loginId = :loginId
		    AND h.crew.crewId = :crewId
		    AND h.status = :status
		    """)
		boolean existsByEmployee_LoginIdAndCrew_CrewIdAndStatus(
		        @Param("loginId") String loginId,
		        @Param("crewId") Integer crewId,
		        @Param("status") String status);

	
    // 내가 가입한 크루 조회
    @Query("""
        SELECT h
        FROM CrewMemberHist h
        JOIN FETCH h.crew
        WHERE h.employee.loginId = :loginId
        AND h.status = 'ACTIVE'
        """)
	List<CrewMemberHist> findMyCrewList(@Param("loginId")String loginId, String string);

	
    // 크루 탈퇴
    @Modifying
    @Query("""
        UPDATE CrewMemberHist h
        SET h.status = 'LEFT',
            h.leftDate = :leftDate
        WHERE h.employee.loginId = :loginId
        AND h.crew.crewId = :crewId
        AND h.status = 'ACTIVE'
        """)
    int leaveCrew(
            @Param("loginId") String loginId,
            @Param("crewId") int crewId,
            @Param("leftDate") LocalDateTime leftDate);
    
 


}
