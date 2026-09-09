package com.kh.workation.reservation.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.reservation.model.dto.ReservationDetail;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.vo.Reservation;

public interface ReservationService {	
	
	Reservation cancelReservation(int workationId, String reason);

}
