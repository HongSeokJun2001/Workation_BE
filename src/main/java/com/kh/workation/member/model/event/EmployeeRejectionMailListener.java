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
public class EmployeeRejectionMailListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeRejectionMailListener.class);

    private final JavaMailSender mailSender;
    private final String senderAddress;

    public EmployeeRejectionMailListener(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendRejectionMail(EmployeeRejectionEvent event) {
        if (event.email() == null || event.email().isBlank()) {
            log.warn("Employee rejection email skipped because the email address is empty.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.email());
            if (!senderAddress.isBlank()) {
                message.setFrom(senderAddress);
            }
            message.setSubject("[Workation] 회원가입 신청 결과 안내");
            message.setText("안녕하세요, " + event.employeeName() + "님.\n\n"
                    + "Workation 회원가입 신청이 승인되지 않았습니다.\n"
                    + "자세한 사항은 소속 회사 관리자에게 문의해주세요.\n\n"
                    + "감사합니다.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Employee rejection email delivery failed. recipient={}", event.email(), e);
        }
    }
}