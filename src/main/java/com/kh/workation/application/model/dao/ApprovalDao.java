package com.kh.workation.application.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.application.model.vo.Approval;

public interface ApprovalDao extends JpaRepository<Approval, Integer> {
	
}