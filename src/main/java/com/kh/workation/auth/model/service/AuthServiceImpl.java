package com.kh.workation.auth.model.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.workation.auth.model.dao.AdminAuthDao;
import com.kh.workation.auth.model.dao.AuthDao;
import com.kh.workation.auth.model.dto.LoginRequest;
import com.kh.workation.auth.model.dto.LoginResponse;
import com.kh.workation.member.model.vo.Admin;
import com.kh.workation.member.model.vo.Employee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthDao authDao;
    private final AdminAuthDao adminAuthDao;
    private final PasswordEncoder passwordEncoder;
    private final Key signingKey;

    public AuthServiceImpl(
            AuthDao authDao,
            AdminAuthDao adminAuthDao,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.secret}") String secretKey) {
        this.authDao = authDao;
        this.adminAuthDao = adminAuthDao;
        this.passwordEncoder = passwordEncoder;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if ("ADMIN".equalsIgnoreCase(request.getLoginType())) {
            Admin admin = adminAuthDao.findByLoginIdAndStatus(request.getLoginId(), Admin.STATUS_ACTIVE).orElse(null);

            if (admin != null && passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                return LoginResponse.builder()
                    .accessToken(generateToken(admin.getLoginId(),admin.getAdminId(), admin.getRole(), admin.getCompanyId()))
                        .tokenType("Bearer")
                        .role(admin.getRole())
                        .build();
            }

            throw new IllegalArgumentException("관리자 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if ("EMPLOYEE".equalsIgnoreCase(request.getLoginType())) {
            Employee employee = authDao.findByLoginIdAndStatus(request.getLoginId(), Employee.STATUS_ACTIVE).orElse(null);

            if (employee != null && passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                return LoginResponse.builder()
                    .accessToken(generateToken(employee.getLoginId(), null, Admin.ROLE_EMPLOYEE, employee.getCompanyId()))
                        .tokenType("Bearer")
                        .role(Admin.ROLE_EMPLOYEE)
                        .build();
            }

            throw new IllegalArgumentException("직원 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        throw new IllegalArgumentException("로그인 유형이 올바르지 않습니다.");
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAdminToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);

            return Admin.ROLE_SUPER_ADMIN.equals(role) || Admin.ROLE_COMPANY_ADMIN.equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isSuperAdminToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Admin.ROLE_SUPER_ADMIN.equals(claims.get("role", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isCompanyAdminToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Admin.ROLE_COMPANY_ADMIN.equals(claims.get("role", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getCompanyId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Number companyId = claims.get("companyId", Number.class);
            return companyId == null ? null : companyId.longValue();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getLoginId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Long getAdminId(String token) {
    	try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Number adminId = claims.get("adminId", Number.class);
            return adminId == null ? null : adminId.longValue();
        } catch (Exception e) {
            return null;
        }
    	
    }

    private String generateToken(String loginId, Long adminId, String role, Long companyId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 3600_000);

        var tokenBuilder = Jwts.builder()
                .setSubject(loginId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry);
        
        if (adminId != null) {
            tokenBuilder.claim("adminId", adminId);
        }

        if (companyId != null) {
            tokenBuilder.claim("companyId", companyId);
        }

        return tokenBuilder
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    
    //employee 롤만 크루 글 작성 신청 삭제 수정 가능
    @Override
    public boolean isEmployeeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Admin.ROLE_EMPLOYEE.equals(
                    claims.get("role", String.class)
            );

        } catch (Exception e) {
            return false;
        }
    }
    
    
    
    
    
    
}
