package com.kh.workation.crew.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kh.workation.crew.model.vo.Crew;

public interface CrewDao extends JpaRepository<Crew, Integer>{

	Page<Crew> findByStatusOrderByCrewIdDesc(String status, Pageable pageable);


	Page<Crew> findByCrewNameContainingAndStatusOrderByCrewIdDesc(String keyword, String string, Pageable pageable);
	
	

}

