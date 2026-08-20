package com.kh.workation.member.model.service;

import java.util.List;

import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.member.model.vo.Employee;

public interface MemberService {

    // 회원 목록 조회용 서비스
    List<Admin> selectAdminList(String status, String target);

    List<Admin> selectCompanyAdminList(String status);

    List<Employee> selectEmployeeList(String status);

}
