package com.kh.workation.application.model.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kh.workation.application.model.vo.Application;

@Repository
public interface ApplicationDao extends JpaRepository<Application, Integer> {

	@Query("SELECT a FROM Application a " +
	           "LEFT JOIN a.progress p " +
	           "WHERE (p.status = :status OR (p IS NULL AND :status = 'APPLY')) " +
	           "ORDER BY a.workationId DESC")
    Page<Application> findByProgressStatus(@Param("status") String status, Pageable pageable);
	
	@EntityGraph(attributePaths = {
	        "crew",
	        "crew.employee",
	        "facility",
	        "reservationDate"
	    })
	    Optional<Application> findByWorkationId(int workationId);
	
}
