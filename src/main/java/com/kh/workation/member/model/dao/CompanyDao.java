package com.kh.workation.member.model.dao;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.member.model.vo.Company;

public interface CompanyDao extends JpaRepository<Company, Long> {

	Optional<Company> findByBusinessNoAndCompanyName(String businessNo, String companyName);


	List<Company> findByCompanyStatus(String companyStatus);

	Page<Company> findByCompanyStatus(String companyStatus, Pageable pageable);

	boolean existsByBusinessNo(String businessNo);

	boolean existsByBusinessNoAndCompanyIdNot(String businessNo, Long companyId);
}
