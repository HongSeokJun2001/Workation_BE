package com.kh.workation.application.model.dao;

import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kh.workation.application.model.vo.Application;

@Repository
public interface ApplicationDao extends JpaRepository<Application, Integer> {

	@EntityGraph(attributePaths = {
	        "crew", 
	        "crew.employee", 
	        "facility", 
	        "reservationDate",
	        "progress"
	    })
	    Page<Application> findAll(Specification<Application> spec, Pageable pageable);
	
	@EntityGraph(attributePaths = {
		    "crew", 
		    "crew.employee", 
		    "facility", 
		    "reservationDate",
		    "progress"
		})
		Page<Application> findByCompanyCompanyIdOrderByWorkationIdDesc(@Param("companyId") Long companyId, Pageable pageable);
	
	
	@EntityGraph(attributePaths = {
	        "crew",
	        "crew.employee",
	        "facility",
	        "reservationDate",
	        "progress",
	        "approval",
	        "reservation"
	    })
	    Optional<Application> findByWorkationId(int workationId);

	@Query("""
		SELECT COUNT(a)
		FROM Application a
		WHERE a.company.companyId = :companyId
		AND a.progress.status = 'APPLY'
		""")
	long countPendingApplicationsByCompany(@Param("companyId") Long companyId);

	@Query("""
		SELECT COUNT(a)
		FROM Application a
		WHERE a.company.companyId = :companyId
		AND a.progress.status = 'CONFIRM'
		AND a.progress.confirmDate BETWEEN :startDate AND :endDate
		""")
	long countConfirmedApplicationsByCompanyAndConfirmDateBetween(
			@Param("companyId") Long companyId,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
		SELECT COUNT(DISTINCT a)
		FROM Application a
		JOIN a.crew.crewMemberHists h
		WHERE h.employee.loginId = :loginId
		AND h.status = 'ACTIVE'
		AND a.progress.status = 'APPLY'
		""")
	long countPendingApplicationsByCrewMember(@Param("loginId") String loginId);

	@Query("""
		SELECT COUNT(DISTINCT a)
		FROM Application a
		JOIN a.crew.crewMemberHists h
		WHERE h.employee.loginId = :loginId
		AND h.status = 'ACTIVE'
		AND a.progress.status = 'CONFIRM'
		""")
	long countConfirmedApplicationsByCrewMember(@Param("loginId") String loginId);
	
}
