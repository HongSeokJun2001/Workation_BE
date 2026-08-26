package com.kh.workation.member.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuperAdminCreateRequest {

    private String loginId;
    private String password;
}