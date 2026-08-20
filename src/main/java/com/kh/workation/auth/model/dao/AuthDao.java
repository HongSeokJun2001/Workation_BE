package com.kh.workation.auth.model.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Employee;

public interface AuthDao extends JpaRepository<Employee, Long> {

	Optional<Employee> findByLoginIdAndStatus(String loginId, String status);
	
}
