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

	@EntityGraph(attributePaths = {
		    "crew", 
		    "crew.employee", 
		    "facility", 
		    "reservationDate",
		    "progress"
		})
		Page<Application> findAllByOrderByWorkationIdDesc(Pageable pageable);
	
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
	
}
