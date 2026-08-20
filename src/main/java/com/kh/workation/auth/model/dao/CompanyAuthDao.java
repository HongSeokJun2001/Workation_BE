package com.kh.workation.auth.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Company;

public interface CompanyAuthDao extends JpaRepository<Company, Long> {

	Optional<Company> findFirstByOrderByCompanyIdAsc();

	boolean existsByBusinessNo(String businessNo);
}