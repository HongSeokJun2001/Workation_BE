package com.kh.workation.application.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.dto.ApplicationList;
import com.kh.workation.application.model.vo.Application;

public interface ApplicationService {
	
	Page<ApplicationList> getApplicationList(Pageable pageable, Long companyId);
	
	Page<ApplicationList> getApplicationMemberList(Pageable pageable, String loginId);
	
	ApplicationDetail getApplicationDetail(int workationId);
	
	ApplicationDetail getApplicationMemberDetail(int workationId);
	
	Application insertApplication(Application a);
	
	Application approveApplication(int workationId, Long AdminId);
	
	Application cancelApplication(int workationId, Long AdminId, String reason);
	
	
}
