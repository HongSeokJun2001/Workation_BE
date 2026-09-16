package com.kh.workation.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.application.model.service.ApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor	
public class WorkationBatchScheduler {
	
	
	private final ApplicationService ApplicationService;
	
	@Scheduled(cron = "0 30 0 * * *")
    @Transactional
    public void completeFinishedWorkations() {

        try {
            // 1. 어제 날짜로 종료되었으나 아직 COMPLETE가 아닌 데이터 상태 변경
            ApplicationService.updateFinishedWorkationStatus();
            
        } catch (Exception e) {
            log.error("[Batch] 워케이션 자동 완료 처리 중 오류 발생: ", e);
        }
    } 

}
