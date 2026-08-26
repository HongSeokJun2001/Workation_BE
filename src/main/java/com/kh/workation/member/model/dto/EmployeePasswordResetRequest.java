package com.kh.workation.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeePasswordResetRequest {

    private String resetToken;
    private String password;
}