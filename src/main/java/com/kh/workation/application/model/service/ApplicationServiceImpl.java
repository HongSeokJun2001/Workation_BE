package com.kh.workation.application.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.application.model.dao.ApplicationDao;
import com.kh.workation.application.model.dao.ApprovalDao;
import com.kh.workation.application.model.dao.ProgressDao;
import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.vo.Application;
import com.kh.workation.application.model.vo.Approval;
import com.kh.workation.application.model.vo.Progress;
import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.dao.ReservationDateDao;
import com.kh.workation.reservation.model.vo.Reservation;
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
    
    @Autowired
    private ReservationDao reservationDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Application> getApplicationList(Pageable pageable) {
		
		return applicationDao.findByProgressStatus("APPLY", pageable);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ApplicationDetail getApplicationDetail(int applicationId) {
		
		Application application = applicationDao.findByWorkationId(applicationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 신청 내역을 찾을 수 없습니다. id=" + applicationId));
		
		return new ApplicationDetail(application);
	}
	
	@Override
	@Transactional
    public Application insertApplication(Application a) {

        // ReservationDate(예약날짜)
        if (a.getReservationDate() != null) {
            ReservationDate savedDate = reservationDateDao.save(a.getReservationDate());
            a.setReservationDate(savedDate);
        }

        // Application(신청 정보)
        Application savedApp = applicationDao.save(a);

        // Progress(진행상태)
        Progress progress = new Progress();
        progress.setWorkationId(savedApp.getWorkationId()); 
        progress.setStatus("APPLY");                        
        progressDao.save(progress);

        return savedApp;
    }
	
	@Override
	@Transactional
	public Application approveApplication(int workationId, Long adminId) {
		
		Application app = applicationDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
		
		Progress progress = progressDao.findById(workationId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
		progress.setStatus("CONFIRM");                        
        
        Reservation reservation = new Reservation();
        reservation.setApplication(app);
        reservation.setFacility(app.getFacility());
        reservation.setReservationDate(app.getReservationDate());
        reservation.setStatus("RESERVED");

        reservationDao.save(reservation);
        
        Approval approval = new Approval();
        approval.setWorkationId(workationId);
        approval.setAdminId(adminId);
        approval.setApprovedYn("APPROVED");        
        approvalDao.save(approval);
		
        return app;
	}
	
	@Override
	@Transactional
	public Application cancelApplication(int workationId, Long adminId, String reason) {
	    
	    Application app = applicationDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
	    
	    Progress progress = progressDao.findById(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청 건입니다."));
	    progress.setStatus("CANCEL");
	    
	    Approval approval = new Approval();
	    approval.setWorkationId(workationId);
	    approval.setAdminId(adminId);
	    approval.setApprovedYn("REJECT");
	    approval.setRejectReason(reason);
	    approvalDao.save(approval);
	    
	    return app;
	}
	
	
}
