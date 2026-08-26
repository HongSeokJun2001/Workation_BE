package com.kh.workation.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFindIdRequest {

    private Long empNo;
    private String employeeName;
    private String phone;
    private String email;
}