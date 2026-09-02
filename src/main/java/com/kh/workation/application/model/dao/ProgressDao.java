package com.kh.workation.application.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.application.model.vo.Progress;

public interface ProgressDao extends JpaRepository<Progress, Integer> {
	
	Optional<Progress> findByWorkationId(int workationId);
	
}
