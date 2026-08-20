package com.kh.workation.auth.model.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Admin;

public interface AdminAuthDao extends JpaRepository<Admin, Long> {

    Optional<Admin> findByLoginIdAndStatus(String loginId, String status);

    boolean existsByRoleAndStatus(String role, String status);
}
