package com.kh.workation.reservation.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.reservation.model.vo.ReservationDate;

public interface ReservationDateDao extends JpaRepository<ReservationDate, Integer> {
	
}