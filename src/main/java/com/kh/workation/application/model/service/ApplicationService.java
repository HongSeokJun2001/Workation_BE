package com.kh.workation.application.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.vo.Application;

public interface ApplicationService {
	
	Page<Application> getApplicationList(Pageable pageable);
	
	ApplicationDetail getApplicationDetail(int workationId);
	
	Application insertApplication(Application a);
	
	Application approveApplication(int workationId, Long AdminId);
	
	Application cancelApplication(int workationId, Long AdminId, String reason);
	
}
