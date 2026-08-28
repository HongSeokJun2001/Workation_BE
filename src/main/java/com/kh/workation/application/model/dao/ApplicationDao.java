package com.kh.workation.application.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.workation.application.model.vo.Application;

@Repository
public interface ApplicationDao extends JpaRepository<Application, Integer> {

	Page<Application> findByProgressStatusOrderByWorkationIdDesc(String status, Pageable pageable);
	
}
