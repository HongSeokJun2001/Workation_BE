package com.kh.workation.reservation.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kh.workation.reservation.model.dao.ReservationDao;
import com.kh.workation.reservation.model.vo.Reservation;

@Service
public class ReservationServiceImpl implements ReservationService{
	
	@Autowired
    private ReservationDao reservationDao;
	
	@Override
	public Page<Reservation> getReservationList(Pageable pageable) {
		
		return reservationDao.findAllByOrderByReservationIdDesc(pageable);
	}

}
