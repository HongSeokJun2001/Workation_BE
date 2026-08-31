package com.kh.workation.application.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.application.model.dto.ApplicationDetail;
import com.kh.workation.application.model.dto.ApplicationList;
import com.kh.workation.application.model.service.ApplicationService;
import com.kh.workation.application.model.vo.Application;
import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.member.model.vo.Company;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
public class ApplicationController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private AuthService authService;

	@GetMapping("/application/list")
	public ResponseEntity<HashMap<String, Object>> getApplicationList(
			@RequestParam(value="cpage", defaultValue="1") int currentPage){
		
		int pageLimit = 10;
		int boardLimit = 10;
		
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
		Page<Application> page = applicationService.getApplicationList(pageable);
		
		List<ApplicationList> list = page.getContent().stream()
	            .map(ApplicationList::new)
	            .toList();
		
		long listCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, 
				pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK)
				 .body(hm);
	}
	
	@GetMapping("/application/{workationId}")
	public ResponseEntity<ApplicationDetail> getApplicationDetail(@PathVariable("workationId") int workationId) {
		ApplicationDetail detail = applicationService.getApplicationDetail(workationId);
        return ResponseEntity.ok(detail);
    }
	
	@PostMapping("/application/insert")
	public ResponseEntity<String> insertApplication(@RequestBody Application a, HttpServletRequest request){
		
		String authHeader = request.getHeader("Authorization");
		
		String token = authHeader.substring(7);
	    String loginId = authService.getLoginId(token);
	    
	    Long companyId = authService.getCompanyId(token);
	    
	    Company company = new Company();
	    company.setCompanyId(companyId);
	    
	    a.setCompany(company);
		
		Application insertAp = applicationService.insertApplication(a);
		
		String message = (insertAp != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
				 .body(message);
	}
}
