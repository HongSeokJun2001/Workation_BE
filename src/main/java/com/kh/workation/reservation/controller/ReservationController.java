package com.kh.workation.reservation.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.reservation.model.dto.ReservationList;
import com.kh.workation.reservation.model.service.ReservationService;

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
	

}
