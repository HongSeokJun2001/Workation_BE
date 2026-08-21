package com.kh.workation.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.reservation.model.service.ReservationService;

public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;

}
