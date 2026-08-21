package com.kh.workation.application.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.vo.Application;

@Service
public class ApplicationServiceImpl implements ApplicationService{
	
	@Autowired
	private ApplicationDao applicationDao;
	
	public Page<Application> selectApplicationList(Pageable pageable) {
		
		return applicationDao.findByProgressStatusOrderByWorkationIdDesc("APPLY", pageable);
	}
	
}
