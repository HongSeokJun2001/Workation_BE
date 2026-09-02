package com.kh.workation.reservation.model.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.workation.reservation.model.vo.Reservation;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Integer>{

	@EntityGraph(attributePaths = {
	        "application", 
	        "application.crew", 
	        "application.crew.employee", 
	        "facility", 
	        "reservationDate"
	    })
	Page<Reservation> findAllByOrderByReservationIdDesc(Pageable pageable);
	
	@EntityGraph(attributePaths = {
	        "application", 
	        "application.crew", 
	        "application.crew.employee", 
	        "facility", 
	        "reservationDate"
	    })
	    Optional<Reservation> findByReservationId(int reservationId);
	
	@EntityGraph(attributePaths = {
	        "application", 
	        "application.crew", 
	        "application.crew.employee", 
	        "facility", 
	        "reservationDate"
	    })
	    Optional<Reservation> findByApplication_WorkationId(int workationId);
}
