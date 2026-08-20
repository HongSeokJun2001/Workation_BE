package com.kh.workation.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Admin;

public interface AdminDao extends JpaRepository<Admin, Long> {

    List<Admin> findByRole(String role);

    List<Admin> findByRoleAndStatus(String role, String status);

    List<Admin> findByRoleIn(List<String> roles);

    List<Admin> findByRoleInAndStatus(List<String> roles, String status);
}
