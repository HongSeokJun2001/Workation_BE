package com.kh.workation.auth.model.service;

import com.kh.workation.auth.model.dto.LoginRequest;
import com.kh.workation.auth.model.dto.LoginResponse;

public interface AuthService {

	LoginResponse login(LoginRequest request);

	boolean isValidToken(String token);

	boolean isAdminToken(String token);

	boolean isSuperAdminToken(String token);

	boolean isCompanyAdminToken(String token);

	Long getCompanyId(String token);

	String getLoginId(String token);
}
