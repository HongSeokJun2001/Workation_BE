package com.kh.workation.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

    List<Employee> findByStatus(String status);
}
