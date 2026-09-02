package com.kh.workation.member.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.member.model.dto.AdminDetailResponse;
import com.kh.workation.member.model.dto.AdminListResponse;
import com.kh.workation.member.model.dto.AdminUpdateRequest;
import com.kh.workation.member.model.dto.CompanyAdminCreateRequest;
import com.kh.workation.member.model.dto.CompanyCreateRequest;
import com.kh.workation.member.model.dto.CompanyResponse;
import com.kh.workation.member.model.dto.CompanyUpdateRequest;
import com.kh.workation.member.model.dto.SuperAdminCreateRequest;
import com.kh.workation.member.model.vo.Company;
import com.kh.workation.member.model.dto.EmployeeDetailResponse;
import com.kh.workation.member.model.dto.EmployeeSignupRequest;
import com.kh.workation.member.model.dto.EmployeeUpdateRequest;
import com.kh.workation.member.model.vo.Employee;

public interface MemberService {

    // 회원 목록 조회용 서비스
    List<AdminListResponse> selectAdminList(String status, String target);

    Page<AdminListResponse> selectAdminPage(String status, String target, Pageable pageable);

    List<AdminListResponse> selectCompanyAdminList(String status, Long companyId);

    Page<AdminListResponse> selectCompanyAdminPage(String status, Long companyId, Pageable pageable);

    List<Company> selectActiveCompanyList();

    Page<CompanyResponse> selectCompanyPage(String status, Pageable pageable);

    CompanyResponse selectCompanyDetail(Long companyId);

    CompanyResponse createCompany(CompanyCreateRequest request);

    CompanyResponse updateCompany(Long companyId, CompanyUpdateRequest request);

    List<Employee> selectEmployeeList(String status, String isProgressed, Long companyId);

    Page<Employee> selectEmployeePage(String status, String isProgressed, Long companyId, Pageable pageable);

    EmployeeDetailResponse selectEmployeeSelf(String loginId, Long companyId);

    AdminDetailResponse selectAdminDetail(Long adminId);

    AdminDetailResponse selectCompanyAdminDetail(Long adminId, Long companyId);

    EmployeeDetailResponse selectEmployeeDetail(Long employeeId, Long companyId);

    AdminDetailResponse updateAdmin(Long adminId, AdminUpdateRequest request);

    AdminDetailResponse updateCompanyAdmin(Long adminId, Long companyId, AdminUpdateRequest request);

    AdminDetailResponse createCompanyAdmin(Long companyId, CompanyAdminCreateRequest request);

    AdminDetailResponse createCompanyAdminBySuper(CompanyAdminCreateRequest request);

    AdminDetailResponse createSuperAdmin(SuperAdminCreateRequest request);

    EmployeeDetailResponse updateEmployee(Long employeeId, Long companyId, EmployeeUpdateRequest request);

    EmployeeDetailResponse approveEmployee(Long employeeId, Long companyId);

    void rejectEmployee(Long employeeId, Long companyId);

    EmployeeDetailResponse updateEmployeeSelf(String loginId, Long companyId, EmployeeUpdateRequest request);

    boolean existsCompany(String businessNo, String companyName);

    boolean existsEmployeeLoginId(String loginId);

    long countActiveCompanies();

    Employee signupEmployee(EmployeeSignupRequest request);

}
