package com.kh.workation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kh.workation.auth.model.dao.AdminAuthDao;
import com.kh.workation.member.model.vo.Admin;

@Configuration
public class InitialAdminDataConfig {

	@Bean
	CommandLineRunner initializeSuperAdmin(
			AdminAuthDao adminAuthDao,
			PasswordEncoder passwordEncoder,
			@Value("${app.init.super-admin.login-id:admin}") String loginId,
			@Value("${app.init.super-admin.password:test}") String password) {

		return args -> {
			if (adminAuthDao.existsByRoleAndStatus(Admin.ROLE_SUPER_ADMIN, Admin.STATUS_ACTIVE)) {
				return;
			}

			Admin admin = new Admin();
			admin.setCompanyId(null);
			admin.setLoginId(loginId);
			admin.setPassword(passwordEncoder.encode(password));
			admin.setRole(Admin.ROLE_SUPER_ADMIN);
			admin.setStatus(Admin.STATUS_ACTIVE);
			adminAuthDao.save(admin);
		};
	}
}