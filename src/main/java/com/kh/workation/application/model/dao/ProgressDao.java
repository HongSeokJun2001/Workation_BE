package com.kh.workation.application.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.application.model.vo.Progress;

public interface ProgressDao extends JpaRepository<Progress, Integer> {
	
}
