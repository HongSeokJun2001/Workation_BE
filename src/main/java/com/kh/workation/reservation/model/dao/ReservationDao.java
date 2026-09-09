package com.kh.workation.reservation.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kh.workation.reservation.model.vo.Reservation;
import com.kh.workation.facility.model.vo.Facility;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Integer>{

	long countByStatus(String status);

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

	@Query("""
		SELECT COUNT(DISTINCT r.facility.facilityId)
		FROM Reservation r
		JOIN r.application.crew.crewMemberHists h
		WHERE h.employee.loginId = :loginId
		AND h.status = 'ACTIVE'
		AND r.status = 'COMPLETED'
		AND NOT EXISTS (
			SELECT review.reviewId
			FROM Review review
			WHERE review.employee.loginId = :loginId
			AND review.facility.facilityId = r.facility.facilityId
		)
		""")
	long countReviewableFacilities(@Param("loginId") String loginId);

	@Query("""
		SELECT DISTINCT r.facility
		FROM Reservation r
		JOIN r.application.crew.crewMemberHists h
		WHERE h.employee.loginId = :loginId
		AND h.status = 'ACTIVE'
		AND r.status = 'COMPLETED'
		AND NOT EXISTS (
			SELECT review.reviewId
			FROM Review review
			WHERE review.employee.loginId = :loginId
			AND review.facility.facilityId = r.facility.facilityId
		)
		ORDER BY r.facility.facilityId DESC
		""")
	List<Facility> findReviewableFacilities(@Param("loginId") String loginId);
}
