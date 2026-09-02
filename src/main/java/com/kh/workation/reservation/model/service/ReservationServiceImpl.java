package com.kh.workation.reservation.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.vo.Reservation;

@Service
public class ReservationServiceImpl implements ReservationService{
	
	@Autowired
    private ReservationDao reservationDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<ReservationList> getReservationList(Pageable pageable) {
	    
	    Page<Reservation> page = reservationDao.findAllByOrderByReservationIdDesc(pageable);
	    
	    return page.map(ReservationList::new);
	}

}
