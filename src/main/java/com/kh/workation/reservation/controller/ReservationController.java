package com.kh.workation.reservation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.reservation.model.dto.ReservationDetail;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.service.ReservationService;
import com.kh.workation.reservation.model.vo.Reservation;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@GetMapping("/reservation/list")
	public ResponseEntity<HashMap<String, Object>> getReservationList(
			@RequestParam(value="cpage", defaultValue="1") int currentPage){
		
		int pageLimit = 10;
		int boardLimit = 10;
		
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
		Page<ReservationList> page = reservationService.getReservationList(pageable);
		
		List<ReservationList> list = page.getContent();
		
		long listCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, 
				pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK)
				 .body(hm);
	}
	
	@GetMapping("/reservation/{reservationId}")
	public ResponseEntity<ReservationDetail> getReservationDetail(@PathVariable("reservationId") int reservationId) {
		ReservationDetail detail = reservationService.getReservationDetail(reservationId);
        return ResponseEntity.ok(detail);
    }
	
	@PutMapping("/reservation/cancel/{workationId}")
	public ResponseEntity<String> cancelReservation(@PathVariable("workationId") int workationId, @RequestBody Map<String, String> body, HttpServletRequest request) {
		
		String authHeader = request.getHeader("Authorization");
		
		String token = authHeader.substring(7);
		
		String reason = body.get("reason");
		
	    Reservation r = reservationService.cancelReservation(workationId, reason);
	    
	    String message = (r != null) ? "success" : "fail";
	    
	    return ResponseEntity.status(HttpStatus.OK)
				 .body(message);
	}
	

}
