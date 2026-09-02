package com.kh.workation.reservation.model.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.application.model.dao.ProgressDao;
import com.kh.workation.application.model.vo.Progress;
import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.dto.ReservationDetail;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.vo.Reservation;

@Service
public class ReservationServiceImpl implements ReservationService{
	
	@Autowired
    private ReservationDao reservationDao;
	
	@Autowired
	private ProgressDao progressDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<ReservationList> getReservationList(Pageable pageable) {
	    
	    Page<Reservation> page = reservationDao.findAllByOrderByReservationIdDesc(pageable);
	    
	    return page.map(ReservationList::new);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ReservationDetail getReservationDetail(int reservationId) {
		
		Reservation reservation = reservationDao.findByReservationId(reservationId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 예약 내역을 찾을 수 없습니다. id=" + reservationId));
		
		return new ReservationDetail(reservation);
	}
	
	@Override
	@Transactional
	public Reservation cancelReservation(int workationId, String reason) {
	    
		// workationId로 예약 정보 조회
	    Reservation reservation = reservationDao.findByApplication_WorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 건입니다. workationId=" + workationId));
	    
	    // workationId로 Progress 조회
	    Progress progress = progressDao.findByWorkationId(workationId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진행 상태 건입니다. workationId=" + workationId));
	    
	    // 상태 변경 및 취소 정보 저장 
	    progress.setStatus("CANCELLED");
	    
	    reservation.setStatus("CANCELLED");
	    reservation.setCancelledReason(reason);       // 취소 사유 저장
	    reservation.setCancelledDate(LocalDate.now()); // 취소 일자 저장 (현재 날짜)
	    
	    return reservation;
	}

}
