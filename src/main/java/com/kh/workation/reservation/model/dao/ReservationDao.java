package com.kh.workation.reservation.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.workation.reservation.model.vo.Reservation;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Integer>{

	Page<Reservation> findAllByOrderByReservationIdDesc(Pageable pageable);
}
