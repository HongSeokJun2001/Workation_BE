package com.kh.workation.member.model.service;

import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.member.model.dao.CompanyDao;
import com.kh.workation.member.model.dao.AdminDao;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.dto.AdminDetailResponse;
import com.kh.workation.member.model.dto.AdminListResponse;
import com.kh.workation.member.model.dto.AdminUpdateRequest;
import com.kh.workation.member.model.dto.EmployeeDetailResponse;
import com.kh.workation.member.model.dto.EmployeeSignupRequest;
import com.kh.workation.member.model.dto.EmployeeUpdateRequest;
import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.member.model.vo.Company;
import com.kh.workation.member.model.vo.Employee;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[^A-Za-z0-9]).{8,15}$");

	@Autowired
	private AdminDao adminDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CompanyDao companyDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// ADMIN 테이블에서 최고관리자와 본사관리자를 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<AdminListResponse> selectAdminList(String status, String target) {
		if ("SUPER".equalsIgnoreCase(target)) {
			return toAdminListResponse(isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_SUPER_ADMIN)
					: adminDao.findByRoleAndStatus(Admin.ROLE_SUPER_ADMIN, status));
		}

		if ("COMPANY".equalsIgnoreCase(target)) {
			return toAdminListResponse(isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_COMPANY_ADMIN)
					: adminDao.findByRoleAndStatus(Admin.ROLE_COMPANY_ADMIN, status));
		}

		if (isAllStatus(status)) {
			return toAdminListResponse(adminDao.findByRoleIn(
					Arrays.asList(Admin.ROLE_SUPER_ADMIN, Admin.ROLE_COMPANY_ADMIN)));
		}

		return toAdminListResponse(adminDao.findByRoleInAndStatus(
				Arrays.asList(Admin.ROLE_SUPER_ADMIN, Admin.ROLE_COMPANY_ADMIN),
				status));
	}

	// ADMIN 테이블에서 본사관리자만 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<AdminListResponse> selectCompanyAdminList(String status, Long companyId) {
		if (isAllStatus(status)) {
			return toAdminListResponse(adminDao.findByCompanyIdAndRole(companyId, Admin.ROLE_COMPANY_ADMIN));
		}

		return toAdminListResponse(adminDao.findByCompanyIdAndRoleAndStatus(companyId, Admin.ROLE_COMPANY_ADMIN, status));
	}

	// EMPLOYEE 테이블에서 활성 직원만 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<Employee> selectEmployeeList(String status, Long companyId) {
		if (isAllStatus(status)) {
			return employeeDao.findByCompanyId(companyId);
		}

		return employeeDao.findByCompanyIdAndStatus(companyId, status);
	}

	@Override
	@Transactional(readOnly = true)
	public AdminDetailResponse selectAdminDetail(Long adminId) {
		Admin admin = adminDao.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("해당 관리자 계정을 찾을 수 없습니다."));

		return AdminDetailResponse.from(admin, findCompanyName(admin.getCompanyId()));
	}

	@Override
	@Transactional(readOnly = true)
	public AdminDetailResponse selectCompanyAdminDetail(Long adminId, Long companyId) {
		Admin admin = adminDao.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("해당 관리자 계정을 찾을 수 없습니다."));

		if (!Admin.ROLE_COMPANY_ADMIN.equals(admin.getRole()) || !companyId.equals(admin.getCompanyId())) {
			throw new IllegalArgumentException("조회 권한이 없는 관리자 계정입니다.");
		}

		return AdminDetailResponse.from(admin, findCompanyName(admin.getCompanyId()));
	}

	@Override
	@Transactional(readOnly = true)
	public EmployeeDetailResponse selectEmployeeDetail(Long employeeId, Long companyId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("조회 권한이 없는 직원 계정입니다.");
		}

		return EmployeeDetailResponse.from(employee, findCompanyName(employee.getCompanyId()));
	}

	@Override
	@Transactional
	public AdminDetailResponse updateAdmin(Long adminId, AdminUpdateRequest request) {
		Admin admin = adminDao.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("해당 관리자 계정을 찾을 수 없습니다."));

		applyAdminUpdate(admin, request);
		return AdminDetailResponse.from(admin, findCompanyName(admin.getCompanyId()));
	}

	@Override
	@Transactional
	public AdminDetailResponse updateCompanyAdmin(Long adminId, Long companyId, AdminUpdateRequest request) {
		Admin admin = adminDao.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("해당 관리자 계정을 찾을 수 없습니다."));

		if (!Admin.ROLE_COMPANY_ADMIN.equals(admin.getRole()) || !companyId.equals(admin.getCompanyId())) {
			throw new IllegalArgumentException("수정 권한이 없는 관리자 계정입니다.");
		}

		applyAdminUpdate(admin, request);
		return AdminDetailResponse.from(admin, findCompanyName(admin.getCompanyId()));
	}

	@Override
	@Transactional
	public EmployeeDetailResponse updateEmployee(Long employeeId, Long companyId, EmployeeUpdateRequest request) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("수정 권한이 없는 직원 계정입니다.");
		}

		employee.setLoginId(request.getLoginId());
		employee.setEmpNo(request.getEmpNo());
		employee.setEmployeeName(request.getEmployeeName());
		employee.setPhone(request.getPhone());
		employee.setEmail(request.getEmail());
		employee.setDepartment(request.getDepartment());
		employee.setPosition(request.getPosition());
		employee.setWorkationAvailDays(request.getWorkationAvailDays());
		employee.setStatus(request.getStatus());
		employee.setHireDate(request.getHireDate());
		employee.setResignDate(request.getResignDate());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			validatePassword(request.getPassword());
			employee.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		return EmployeeDetailResponse.from(employee, findCompanyName(employee.getCompanyId()));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsCompany(String businessNo, String companyName) {
		return companyDao.findByBusinessNoAndCompanyName(businessNo, companyName).isPresent();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsEmployeeLoginId(String loginId) {
		return employeeDao.existsByLoginId(loginId);
	}

	@Override
	@Transactional
	public Employee signupEmployee(EmployeeSignupRequest request) {
		Company company = companyDao.findByBusinessNoAndCompanyName(request.getBusinessNo(), request.getCompanyName())
				.orElseThrow(() -> new IllegalArgumentException("회사 정보가 일치하지 않습니다."));

		if (employeeDao.existsByLoginId(request.getLoginId())) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}

		if (request.getPassword() == null || !PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
			throw new IllegalArgumentException("비밀번호는 8~15자이며 특수문자를 포함해야 합니다.");
		}

		Employee employee = new Employee();
		employee.setCompanyId(company.getCompanyId());
		employee.setLoginId(request.getLoginId());
		employee.setPassword(passwordEncoder.encode(request.getPassword()));
		employee.setEmpNo(request.getEmpNo());
		employee.setEmployeeName(request.getEmployeeName());
		employee.setPhone(request.getPhone());
		employee.setEmail(request.getEmail());
		employee.setDepartment(request.getDepartment());
		employee.setPosition(request.getPosition());
		employee.setWorkationAvailDays(0);
		employee.setStatus(Employee.STATUS_LOCKED);
		employee.setIsProgressed(Employee.PROGRESSED_N);

		return employeeDao.save(employee);
	}

	private boolean isAllStatus(String status) {
		return status == null || status.isBlank() || "ALL".equalsIgnoreCase(status);
	}

	private void applyAdminUpdate(Admin admin, AdminUpdateRequest request) {
		admin.setLoginId(request.getLoginId());
		admin.setStatus(request.getStatus());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			validatePassword(request.getPassword());
			admin.setPassword(passwordEncoder.encode(request.getPassword()));
		}
	}

	private void validatePassword(String password) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new IllegalArgumentException("비밀번호는 8~15자이며 특수문자를 포함해야 합니다.");
		}
	}

	private List<AdminListResponse> toAdminListResponse(List<Admin> adminList) {
		return adminList.stream()
				.map(admin -> AdminListResponse.from(admin, findCompanyName(admin.getCompanyId())))
				.toList();
	}

	private String findCompanyName(Long companyId) {
		if (companyId == null) {
			return null;
		}

		return companyDao.findById(companyId)
				.map(Company::getCompanyName)
				.orElse(null);
	}
}
