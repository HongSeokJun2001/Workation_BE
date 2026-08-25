package com.kh.workation.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

    boolean existsByLoginId(String loginId);

    java.util.Optional<Employee> findByLoginIdAndStatus(String loginId, String status);

    List<Employee> findByStatus(String status);

    List<Employee> findByCompanyId(Long companyId);

    List<Employee> findByCompanyIdAndStatus(Long companyId, String status);
}
