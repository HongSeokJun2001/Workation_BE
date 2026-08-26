package com.kh.workation.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRecoveryVerifyRequest {

    private String requestId;
    private String verificationCode;
}