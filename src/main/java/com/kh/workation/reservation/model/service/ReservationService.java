package com.kh.workation.reservation.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.reservation.model.vo.Reservation;

public interface ReservationService {
	
	Page<Reservation> getReservationList(Pageable pageable);

}
