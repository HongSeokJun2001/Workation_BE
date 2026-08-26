package com.kh.workation.member.model.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.dto.EmployeeFindIdRequest;
import com.kh.workation.member.model.dto.EmployeeFindPasswordRequest;
import com.kh.workation.member.model.dto.EmployeePasswordResetRequest;
import com.kh.workation.member.model.dto.EmployeeRecoveryVerifyRequest;
import com.kh.workation.member.model.vo.Employee;

@Service
public class EmployeeAccountRecoveryService {

    private static final Duration CODE_VALIDITY = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmployeeDao employeeDao;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final Map<String, RecoveryRequest> requests = new ConcurrentHashMap<>();

    public EmployeeAccountRecoveryService(
            EmployeeDao employeeDao,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderAddress) {
        this.employeeDao = employeeDao;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @Transactional(readOnly = true)
    public String requestLoginId(EmployeeFindIdRequest request) {
        Employee employee = employeeDao.findByEmpNoAndEmployeeNameAndPhoneAndEmailAndStatusAndIsProgressed(
                request.getEmpNo(), request.getEmployeeName(), request.getPhone(), request.getEmail(),
            Employee.STATUS_ACTIVE, Employee.PROGRESSED_Y).orElse(null);

        return issueCode(employee, "아이디 찾기");
    }

    @Transactional(readOnly = true)
    public String requestPasswordReset(EmployeeFindPasswordRequest request) {
        Employee employee = employeeDao.findByLoginIdAndStatusAndIsProgressed(
            request.getLoginId(), Employee.STATUS_ACTIVE, Employee.PROGRESSED_Y).orElse(null);

        return issueCode(employee, "비밀번호 찾기");
    }

    public RecoveryVerification verifyCode(EmployeeRecoveryVerifyRequest request) {
        RecoveryRequest recoveryRequest = requests.get(request.getRequestId());

        if (recoveryRequest == null || recoveryRequest.expiresAt().isBefore(Instant.now())
                || !recoveryRequest.code().equals(request.getVerificationCode())) {
            throw new IllegalArgumentException("인증번호가 올바르지 않거나 만료되었습니다.");
        }

        requests.remove(request.getRequestId(), recoveryRequest);

        if ("아이디 찾기".equals(recoveryRequest.purpose())) {
            return new RecoveryVerification(recoveryRequest.loginId(), null);
        }

        String resetToken = UUID.randomUUID().toString();
        requests.put(resetToken, new RecoveryRequest(
                recoveryRequest.employeeId(), recoveryRequest.loginId(), recoveryRequest.purpose(), resetToken,
            Instant.now().plus(CODE_VALIDITY)));
        return new RecoveryVerification(null, resetToken);
    }

    @Transactional
    public void resetPassword(EmployeePasswordResetRequest request) {
        RecoveryRequest recoveryRequest = requests.remove(request.getResetToken());

        if (recoveryRequest == null || recoveryRequest.expiresAt().isBefore(Instant.now())
                || !"비밀번호 찾기".equals(recoveryRequest.purpose())) {
            throw new IllegalArgumentException("비밀번호 재설정 권한이 없거나 만료되었습니다.");
        }

        if (request.getPassword() == null
                || !request.getPassword().matches("^(?=.*[^A-Za-z0-9]).{8,15}$")) {
            throw new IllegalArgumentException("비밀번호는 8~15자이며 특수문자를 포함해야 합니다.");
        }

        Employee employee = employeeDao.findById(recoveryRequest.employeeId())
            .filter(foundEmployee -> Employee.STATUS_ACTIVE.equals(foundEmployee.getStatus())
                && Employee.PROGRESSED_Y.equals(foundEmployee.getIsProgressed()))
            .orElseThrow(() -> new IllegalArgumentException("해당 직원 계정을 찾을 수 없습니다."));
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private String issueCode(Employee employee, String purpose) {
        if (employee == null || employee.getEmail() == null || employee.getEmail().isBlank()) {
            throw new IllegalArgumentException("입력한 정보와 일치하는 승인된 직원 계정을 찾을 수 없습니다.");
        }

        String requestId = UUID.randomUUID().toString();
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        requests.put(requestId, new RecoveryRequest(
            employee.getEmployeeId(), employee.getLoginId(), purpose, code,
            Instant.now().plus(CODE_VALIDITY)));

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(employee.getEmail());
            if (!senderAddress.isBlank()) {
                message.setFrom(senderAddress);
            }
            message.setSubject("[Workation] " + purpose + " 인증번호");
            message.setText("인증번호는 " + code + "입니다.\n\n인증번호는 5분간 유효합니다.");
            mailSender.send(message);
        } catch (Exception e) {
            requests.remove(requestId);
            throw new IllegalArgumentException("인증번호 이메일 전송에 실패했습니다.");
        }

        return requestId;
    }

        private record RecoveryRequest(
            Long employeeId, String loginId, String purpose, String code, Instant expiresAt) {
    }

    public record RecoveryVerification(String loginId, String resetToken) {
    }
}