package com.kh.workation.member.model.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmployeeApprovalMailListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeApprovalMailListener.class);

    private final JavaMailSender mailSender;
    private final String senderAddress;

    public EmployeeApprovalMailListener(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendApprovalMail(EmployeeApprovalEvent event) {
        if (event.email() == null || event.email().isBlank()) {
            log.warn("Employee approval email skipped because the email address is empty.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.email());
            if (!senderAddress.isBlank()) {
                message.setFrom(senderAddress);
            }
            message.setSubject("[Workation] 회원가입 승인이 완료되었습니다.");
            message.setText("안녕하세요, " + event.employeeName() + "님.\n\n"
                    + "Workation 회원가입 승인이 완료되었습니다. 이제 등록한 아이디로 로그인할 수 있습니다.\n\n"
                    + "감사합니다.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Employee approval email delivery failed. recipient={}", event.email(), e);
        }
    }
}
