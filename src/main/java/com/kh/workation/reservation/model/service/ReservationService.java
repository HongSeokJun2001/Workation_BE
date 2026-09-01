package com.kh.workation.reservation.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.reservation.model.dto.ReservationList;

public interface ReservationService {
	
	Page<ReservationList> getReservationList(Pageable pageable);

}
