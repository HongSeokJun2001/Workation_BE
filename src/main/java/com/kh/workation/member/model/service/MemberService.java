package com.kh.workation.member.model.service;

import java.util.List;

import com.kh.workation.member.model.dto.AdminDetailResponse;
import com.kh.workation.member.model.dto.AdminListResponse;
import com.kh.workation.member.model.dto.AdminUpdateRequest;
import com.kh.workation.member.model.dto.EmployeeDetailResponse;
import com.kh.workation.member.model.dto.EmployeeSignupRequest;
import com.kh.workation.member.model.dto.EmployeeUpdateRequest;
import com.kh.workation.member.model.vo.Employee;

public interface MemberService {

    // 회원 목록 조회용 서비스
    List<AdminListResponse> selectAdminList(String status, String target);

    List<AdminListResponse> selectCompanyAdminList(String status, Long companyId);

    List<Employee> selectEmployeeList(String status, Long companyId);

    AdminDetailResponse selectAdminDetail(Long adminId);

    AdminDetailResponse selectCompanyAdminDetail(Long adminId, Long companyId);

    EmployeeDetailResponse selectEmployeeDetail(Long employeeId, Long companyId);

    AdminDetailResponse updateAdmin(Long adminId, AdminUpdateRequest request);

    AdminDetailResponse updateCompanyAdmin(Long adminId, Long companyId, AdminUpdateRequest request);

    EmployeeDetailResponse updateEmployee(Long employeeId, Long companyId, EmployeeUpdateRequest request);

    boolean existsCompany(String businessNo, String companyName);

    boolean existsEmployeeLoginId(String loginId);

    Employee signupEmployee(EmployeeSignupRequest request);

}
