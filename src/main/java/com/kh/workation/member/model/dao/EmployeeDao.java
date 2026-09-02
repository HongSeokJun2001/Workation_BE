package com.kh.workation.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.member.model.vo.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

    boolean existsByLoginId(String loginId);

    java.util.Optional<Employee> findByLoginIdAndStatus(String loginId, String status);

        java.util.Optional<Employee> findByLoginIdAndStatusAndIsProgressed(
            String loginId, String status, String isProgressed);

        java.util.Optional<Employee> findByCompanyIdAndEmployeeNameAndPhoneAndEmailAndStatusAndIsProgressed(
            Long companyId, String employeeName, String phone, String email,
            String status, String isProgressed);

    List<Employee> findByStatus(String status);

    List<Employee> findByCompanyId(Long companyId);

    List<Employee> findByCompanyIdAndStatus(Long companyId, String status);

    Page<Employee> findByCompanyId(Long companyId, Pageable pageable);

    Page<Employee> findByCompanyIdAndStatus(Long companyId, String status, Pageable pageable);

    Page<Employee> findByCompanyIdAndIsProgressed(Long companyId, String isProgressed, Pageable pageable);

    Page<Employee> findByCompanyIdAndStatusAndIsProgressed(
            Long companyId, String status, String isProgressed, Pageable pageable);
}
