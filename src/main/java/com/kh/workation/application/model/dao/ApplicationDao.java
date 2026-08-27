package com.kh.workation.application.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kh.workation.application.model.vo.Application;

public interface ApplicationDao extends JpaRepository<Application, Integer> {

	//Page<Application> findByProgressStatusOrderByWorkationIdDesc(String status, Pageable pageable);
	
}
