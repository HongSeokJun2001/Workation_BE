package com.kh.workation.application.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.dto.ApplicationList;
import com.kh.workation.application.model.dto.ApplicationSearch;
import com.kh.workation.application.model.vo.Application;

public interface ApplicationService {
	
	Page<ApplicationList> getApplicationList(Pageable pageable, Long companyId, ApplicationSearch searchDto);
	
	Page<ApplicationList> getApplicationMemberList(Pageable pageable, String loginId, ApplicationSearch searchDto);
	
	ApplicationDetail getApplicationDetail(int workationId, Long companyId);
	
	ApplicationDetail getApplicationMemberDetail(int workationId, String loginId);
	
	Application insertApplication(Application a);
	
	Application approveApplication(int workationId, Long AdminId);
	
	Application cancelApplication(int workationId, Long AdminId, String reason, String loginId);
	
	int updateFinishedWorkationStatus();
	
	
}
