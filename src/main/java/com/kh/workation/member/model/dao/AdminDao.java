package com.kh.workation.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.member.model.vo.Admin;

public interface AdminDao extends JpaRepository<Admin, Long> {

    boolean existsByLoginId(String loginId);

    List<Admin> findByRole(String role);

    List<Admin> findByRoleAndStatus(String role, String status);

    List<Admin> findByCompanyIdAndRole(Long companyId, String role);

    List<Admin> findByCompanyIdAndRoleAndStatus(Long companyId, String role, String status);

    Page<Admin> findByCompanyIdAndRole(Long companyId, String role, Pageable pageable);

    Page<Admin> findByCompanyIdAndRoleAndStatus(Long companyId, String role, String status, Pageable pageable);

    List<Admin> findByRoleIn(List<String> roles);

    List<Admin> findByRoleInAndStatus(List<String> roles, String status);

    Page<Admin> findByRole(String role, Pageable pageable);

    Page<Admin> findByRoleAndStatus(String role, String status, Pageable pageable);

    Page<Admin> findByRoleIn(List<String> roles, Pageable pageable);

    Page<Admin> findByRoleInAndStatus(List<String> roles, String status, Pageable pageable);
}
