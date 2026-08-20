package com.kh.workation.notice.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.notice.model.service.NoticeService;
import com.kh.workation.notice.model.vo.Notice;

@CrossOrigin
@RestController
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;
	
	@GetMapping("/notices")
	public ResponseEntity<ArrayList<Notice>> selectNoticeList(){
		
		ArrayList<Notice> list = (ArrayList)noticeService.selectNoticeList();
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
		
	}
	
	

}
