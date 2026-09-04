package com.kh.workation.application.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.application.model.vo.Progress;

public interface ProgressDao extends JpaRepository<Progress, Integer> {
	
	Optional<Progress> findByWorkationId(int workationId);
	
	@Query("SELECT p FROM Progress p, Application a " +
	           "WHERE p.workationId = a.workationId " +
	           "AND a.reservationDate.endDate < :today " +
	           "AND p.status NOT IN ('COMPLETED', 'CANCELLED')")
	    List<Progress> findExpiredProgressList(@Param("today") LocalDate today);
	
}
