package com.kh.workation.application.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.dao.ApprovalDao;
import com.kh.workation.application.model.dao.ProgressDao;
import com.kh.workation.application.model.vo.Application;
import com.kh.workation.application.model.vo.Approval;
import com.kh.workation.application.model.vo.Progress;
import com.kh.workation.member.model.vo.Company;
import com.kh.workation.reservation.model.dao.ReservationDateDao;
import com.kh.workation.reservation.model.vo.ReservationDate;

@Service
public class ApplicationServiceImpl implements ApplicationService{
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
    private ReservationDateDao reservationDateDao;
    
    @Autowired
    private ApprovalDao approvalDao;
    
    @Autowired
    private ProgressDao progressDao;
	
	@Override
	public Page<Application> getApplicationList(Pageable pageable) {
		
		return applicationDao.findByProgressStatusOrderByWorkationIdDesc("APPLY", pageable);
	}
	
	@Override
    @Transactional // 4개 저장 작업 중 하나라도 실패 시 전체 롤백
    public Application insertApplication(Application a) {
        
        // 0. (로그인 전 임시) DB에 존재하는 1번 Company 세팅 (NotNull 에러 방지)
        Company dummyCompany = new Company();
        dummyCompany.setCompanyId(1L); 
        a.setCompany(dummyCompany);

        // ReservationDate(예약날짜)
        if (a.getReservationDate() != null) {
            ReservationDate savedDate = reservationDateDao.save(a.getReservationDate());
            a.setReservationDate(savedDate);
        }

        // Application(신청 정보)
        Application savedApp = applicationDao.save(a);

        // Approval(승인/결재)
        Approval approval = new Approval();
        approval.setWorkationId(savedApp.getWorkationId()); 
        approval.setApprovedYn("PENDING");
        approvalDao.save(approval);

        // Progress(진행상태)
        Progress progress = new Progress();
        progress.setWorkationId(savedApp.getWorkationId()); 
        progress.setStatus("APPLY");                        
        progressDao.save(progress);

        return savedApp;
    }
	
	
}
