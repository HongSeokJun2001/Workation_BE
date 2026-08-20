package com.kh.workation.member.model.service;

import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.member.model.dao.AdminDao;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.member.model.vo.Employee;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private AdminDao adminDao;

	@Autowired
	private EmployeeDao employeeDao;

	// ADMIN 테이블에서 최고관리자와 본사관리자를 조회한다.

	@Override
	@Transactional(readOnly = true)
	public List<Admin> selectAdminList(String status, String target) {
		if ("SUPER".equalsIgnoreCase(target)) {
			return isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_SUPER_ADMIN)
					: adminDao.findByRoleAndStatus(Admin.ROLE_SUPER_ADMIN, status);
		}

		if ("COMPANY".equalsIgnoreCase(target)) {
			return isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_COMPANY_ADMIN)
					: adminDao.findByRoleAndStatus(Admin.ROLE_COMPANY_ADMIN, status);
		}

		if (isAllStatus(status)) {
			return adminDao.findByRoleIn(
					Arrays.asList(Admin.ROLE_SUPER_ADMIN, Admin.ROLE_COMPANY_ADMIN));
		}

		return adminDao.findByRoleInAndStatus(
				Arrays.asList(Admin.ROLE_SUPER_ADMIN, Admin.ROLE_COMPANY_ADMIN),
				status);
	}

	// ADMIN 테이블에서 본사관리자만 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<Admin> selectCompanyAdminList(String status) {
		if (isAllStatus(status)) {
			return adminDao.findByRole(Admin.ROLE_COMPANY_ADMIN);
		}

		return adminDao.findByRoleAndStatus(Admin.ROLE_COMPANY_ADMIN, status);
	}

	// EMPLOYEE 테이블에서 활성 직원만 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<Employee> selectEmployeeList(String status) {
		if (isAllStatus(status)) {
			return employeeDao.findAll();
		}

		return employeeDao.findByStatus(status);
	}

	private boolean isAllStatus(String status) {
		return status == null || status.isBlank() || "ALL".equalsIgnoreCase(status);
	}
}
