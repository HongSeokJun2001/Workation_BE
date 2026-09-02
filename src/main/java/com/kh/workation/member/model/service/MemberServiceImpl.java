package com.kh.workation.member.model.service;

import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.member.model.dao.CompanyDao;
import com.kh.workation.member.model.dao.AdminDao;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.dto.AdminDetailResponse;
import com.kh.workation.member.model.dto.AdminListResponse;
import com.kh.workation.member.model.dto.AdminUpdateRequest;
import com.kh.workation.member.model.dto.CompanyAdminCreateRequest;
import com.kh.workation.member.model.dto.CompanyCreateRequest;
import com.kh.workation.member.model.dto.CompanyResponse;
import com.kh.workation.member.model.dto.CompanyUpdateRequest;
import com.kh.workation.member.model.dto.SuperAdminCreateRequest;
import com.kh.workation.member.model.dto.EmployeeDetailResponse;
import com.kh.workation.member.model.dto.EmployeeSignupRequest;
import com.kh.workation.member.model.dto.EmployeeUpdateRequest;
import com.kh.workation.member.model.event.EmployeeApprovalEvent;
import com.kh.workation.member.model.event.EmployeeRejectionEvent;
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

	@Autowired
	private ApplicationEventPublisher eventPublisher;

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

	@Override
	@Transactional(readOnly = true)
	public Page<AdminListResponse> selectAdminPage(String status, String target, Pageable pageable) {
		List<String> roles = Arrays.asList(Admin.ROLE_SUPER_ADMIN, Admin.ROLE_COMPANY_ADMIN);
		Page<Admin> page;

		if ("SUPER".equalsIgnoreCase(target)) {
			page = isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_SUPER_ADMIN, pageable)
					: adminDao.findByRoleAndStatus(Admin.ROLE_SUPER_ADMIN, status, pageable);
		} else if ("COMPANY".equalsIgnoreCase(target)) {
			page = isAllStatus(status)
					? adminDao.findByRole(Admin.ROLE_COMPANY_ADMIN, pageable)
					: adminDao.findByRoleAndStatus(Admin.ROLE_COMPANY_ADMIN, status, pageable);
		} else {
			page = isAllStatus(status)
					? adminDao.findByRoleIn(roles, pageable)
					: adminDao.findByRoleInAndStatus(roles, status, pageable);
		}

		return page.map(admin -> AdminListResponse.from(admin, findCompanyName(admin.getCompanyId())));
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

	@Override
	@Transactional(readOnly = true)
	public Page<AdminListResponse> selectCompanyAdminPage(String status, Long companyId, Pageable pageable) {
		Page<Admin> page = isAllStatus(status)
				? adminDao.findByCompanyIdAndRole(companyId, Admin.ROLE_COMPANY_ADMIN, pageable)
				: adminDao.findByCompanyIdAndRoleAndStatus(companyId, Admin.ROLE_COMPANY_ADMIN, status, pageable);

		return page.map(admin -> AdminListResponse.from(admin, findCompanyName(admin.getCompanyId())));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Company> selectActiveCompanyList() {
		return companyDao.findByCompanyStatus(Company.STATUS_ACTIVE);
	}

	@Override
	@Transactional(readOnly = true)
	public long countActiveCompanies() {
		return companyDao.findByCompanyStatus(Company.STATUS_ACTIVE).size();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CompanyResponse> selectCompanyPage(String status, Pageable pageable) {
		Page<Company> page = isAllStatus(status)
				? companyDao.findAll(pageable)
				: companyDao.findByCompanyStatus(status.toUpperCase(), pageable);

		return page.map(CompanyResponse::from);
	}

	@Override
	@Transactional(readOnly = true)
	public CompanyResponse selectCompanyDetail(Long companyId) {
		return CompanyResponse.from(findCompany(companyId));
	}

	@Override
	@Transactional
	public CompanyResponse createCompany(CompanyCreateRequest request) {
		String companyName = trimToNull(request.getCompanyName());
		String businessNo = trimToNull(request.getBusinessNo());

		if (companyName == null || businessNo == null) {
			throw new IllegalArgumentException("회사명과 사업자번호를 모두 입력해주세요.");
		}

		if (companyDao.existsByBusinessNo(businessNo)) {
			throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
		}

		Company company = new Company();
		company.setCompanyName(companyName);
		company.setBusinessNo(businessNo);
		company.setCompanyStatus(Company.STATUS_ACTIVE);

		return CompanyResponse.from(companyDao.save(company));
	}

	@Override
	@Transactional
	public CompanyResponse updateCompany(Long companyId, CompanyUpdateRequest request) {
		Company company = findCompany(companyId);

		String companyName = trimToNull(request.getCompanyName());
		String businessNo = trimToNull(request.getBusinessNo());

		if (companyName == null || businessNo == null) {
			throw new IllegalArgumentException("회사명과 사업자번호를 모두 입력해주세요.");
		}

		if (companyDao.existsByBusinessNoAndCompanyIdNot(businessNo, companyId)) {
			throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
		}

		company.setCompanyName(companyName);
		company.setBusinessNo(businessNo);
		company.setCompanyStatus(validateCompanyStatus(request.getStatus()));

		return CompanyResponse.from(company);
	}

	private Company findCompany(Long companyId) {
		return companyDao.findById(companyId)
				.orElseThrow(() -> new IllegalArgumentException("해당 고객사를 찾을 수 없습니다."));
	}

	private String validateCompanyStatus(String status) {
		if (status == null || status.isBlank()) {
			return Company.STATUS_ACTIVE;
		}

		String upperStatus = status.toUpperCase();

		if (!Company.STATUS_ACTIVE.equals(upperStatus) && !Company.STATUS_INACTIVE.equals(upperStatus)) {
			throw new IllegalArgumentException("고객사 상태는 ACTIVE 또는 INACTIVE만 가능합니다.");
		}

		return upperStatus;
	}

	private String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	// EMPLOYEE 테이블에서 조건에 맞는 직원만 조회한다.
	@Override
	@Transactional(readOnly = true)
	public List<Employee> selectEmployeeList(String status, String isProgressed, Long companyId) {
		List<Employee> employees = isAllStatus(status)
				? employeeDao.findByCompanyId(companyId)
				: employeeDao.findByCompanyIdAndStatus(companyId, status);

		if (isAllProgressed(isProgressed)) {
			return employees;
		}

		return employees.stream()
				.filter(employee -> isProgressed.equalsIgnoreCase(employee.getIsProgressed()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Employee> selectEmployeePage(String status, String isProgressed, Long companyId, Pageable pageable) {
		boolean allStatus = isAllStatus(status);
		boolean allProgressed = isAllProgressed(isProgressed);

		if (allStatus && allProgressed) {
			return employeeDao.findByCompanyId(companyId, pageable);
		}
		if (allProgressed) {
			return employeeDao.findByCompanyIdAndStatus(companyId, status, pageable);
		}
		if (allStatus) {
			return employeeDao.findByCompanyIdAndIsProgressed(companyId, isProgressed, pageable);
		}
		return employeeDao.findByCompanyIdAndStatusAndIsProgressed(companyId, status, isProgressed, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public EmployeeDetailResponse selectEmployeeSelf(String loginId, Long companyId) {
		Employee employee = employeeDao.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("조회 권한이 없는 직원 계정입니다.");
		}

		return EmployeeDetailResponse.from(employee, findCompanyName(employee.getCompanyId()));
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

		applyAdminUpdate(admin, request, true);
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

		applyAdminUpdate(admin, request, false);
		return AdminDetailResponse.from(admin, findCompanyName(admin.getCompanyId()));
	}

	@Override
	@Transactional
	public AdminDetailResponse createCompanyAdmin(Long companyId, CompanyAdminCreateRequest request) {
		return createCompanyAdmin(companyId, request, "본사관리자");
	}

	@Override
	@Transactional
	public AdminDetailResponse createCompanyAdminBySuper(CompanyAdminCreateRequest request) {
		if (request.getCompanyId() == null) {
			throw new IllegalArgumentException("회사를 선택해주세요.");
		}

		return createCompanyAdmin(request.getCompanyId(), request, "본사관리자");
	}

	private AdminDetailResponse createCompanyAdmin(Long companyId, CompanyAdminCreateRequest request, String accountType) {
		if (companyDao.findById(companyId)
				.filter(company -> Company.STATUS_ACTIVE.equals(company.getCompanyStatus())).isEmpty()) {
			throw new IllegalArgumentException("선택한 회사가 존재하지 않거나 비활성 상태입니다.");
		}

		if (request.getLoginId() == null || request.getLoginId().isBlank()
				|| request.getPassword() == null || request.getPassword().isBlank()) {
			throw new IllegalArgumentException("아이디와 비밀번호를 입력해주세요.");
		}

		if (adminDao.existsByLoginId(request.getLoginId())) {
			throw new IllegalArgumentException("이미 사용 중인 관리자 아이디입니다.");
		}

		validatePassword(request.getPassword());

		Admin admin = new Admin();
		admin.setCompanyId(companyId);
		admin.setLoginId(request.getLoginId());
		admin.setPassword(passwordEncoder.encode(request.getPassword()));
		admin.setRole(Admin.ROLE_COMPANY_ADMIN);
		admin.setStatus(Admin.STATUS_ACTIVE);

		return AdminDetailResponse.from(adminDao.save(admin), findCompanyName(companyId));
	}

	@Override
	@Transactional
	public AdminDetailResponse createSuperAdmin(SuperAdminCreateRequest request) {
		if (request.getLoginId() == null || request.getLoginId().isBlank()
				|| request.getPassword() == null || request.getPassword().isBlank()) {
			throw new IllegalArgumentException("아이디와 비밀번호를 입력해주세요.");
		}

		if (adminDao.existsByLoginId(request.getLoginId())) {
			throw new IllegalArgumentException("이미 사용 중인 관리자 아이디입니다.");
		}

		validatePassword(request.getPassword());

		Admin admin = new Admin();
		admin.setLoginId(request.getLoginId());
		admin.setPassword(passwordEncoder.encode(request.getPassword()));
		admin.setRole(Admin.ROLE_SUPER_ADMIN);
		admin.setStatus(Admin.STATUS_ACTIVE);

		return AdminDetailResponse.from(adminDao.save(admin), null);
	}

	@Override
	@Transactional
	public EmployeeDetailResponse updateEmployee(Long employeeId, Long companyId, EmployeeUpdateRequest request) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("수정 권한이 없는 직원 계정입니다.");
		}

		if (Employee.PROGRESSED_N.equals(employee.getIsProgressed())
				&& !employee.getStatus().equals(request.getStatus())) {
			throw new IllegalArgumentException("가입 승인 전에는 계정 상태를 변경할 수 없습니다.");
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
	@Transactional
	public void rejectEmployee(Long employeeId, Long companyId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("거부 권한이 없는 직원 계정입니다.");
		}

		if (!Employee.STATUS_LOCKED.equals(employee.getStatus())
				|| !Employee.PROGRESSED_N.equals(employee.getIsProgressed())) {
			throw new IllegalArgumentException("승인 대기 상태의 직원 계정만 거부할 수 있습니다.");
		}

		eventPublisher.publishEvent(new EmployeeRejectionEvent(employee.getEmail(), employee.getEmployeeName()));
		employeeDao.delete(employee);
	}

	@Override
	@Transactional
	public EmployeeDetailResponse approveEmployee(Long employeeId, Long companyId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("승인 권한이 없는 직원 계정입니다.");
		}

		if (!Employee.STATUS_LOCKED.equals(employee.getStatus())
				|| !Employee.PROGRESSED_N.equals(employee.getIsProgressed())) {
			throw new IllegalArgumentException("승인 대기 상태의 직원 계정이 아닙니다.");
		}

		employee.setStatus(Employee.STATUS_ACTIVE);
		employee.setIsProgressed(Employee.PROGRESSED_Y);
		eventPublisher.publishEvent(new EmployeeApprovalEvent(employee.getEmail(), employee.getEmployeeName()));

		return EmployeeDetailResponse.from(employee, findCompanyName(employee.getCompanyId()));
	}

	@Override
	@Transactional
	public EmployeeDetailResponse updateEmployeeSelf(String loginId, Long companyId, EmployeeUpdateRequest request) {
		Employee employee = employeeDao.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));

		if (!companyId.equals(employee.getCompanyId())) {
			throw new IllegalArgumentException("수정 권한이 없는 직원 계정입니다.");
		}

		if (request.getLoginId() != null && !request.getLoginId().isBlank()) {
			employee.setLoginId(request.getLoginId());
		}
		if (request.getPhone() != null && !request.getPhone().isBlank()) {
			employee.setPhone(request.getPhone());
		}
		if (request.getEmail() != null && !request.getEmail().isBlank()) {
			employee.setEmail(request.getEmail());
		}
		if (request.getDepartment() != null) {
			employee.setDepartment(request.getDepartment());
		}
		if (request.getPosition() != null) {
			employee.setPosition(request.getPosition());
		}
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

	private boolean isAllProgressed(String isProgressed) {
		return isProgressed == null || isProgressed.isBlank() || "ALL".equalsIgnoreCase(isProgressed);
	}

	private void applyAdminUpdate(Admin admin, AdminUpdateRequest request, boolean allowCompanyChange) {
		admin.setLoginId(request.getLoginId());
		admin.setStatus(request.getStatus());

		if (allowCompanyChange && Admin.ROLE_COMPANY_ADMIN.equals(admin.getRole())
				&& request.getCompanyId() != null) {
			Company company = companyDao.findById(request.getCompanyId())
					.filter(foundCompany -> Company.STATUS_ACTIVE.equals(foundCompany.getCompanyStatus()))
					.orElseThrow(() -> new IllegalArgumentException("선택한 회사가 존재하지 않거나 비활성 상태입니다."));
			admin.setCompanyId(company.getCompanyId());
		}

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
