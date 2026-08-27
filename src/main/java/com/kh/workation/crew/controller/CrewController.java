package com.kh.workation.crew.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;

@CrossOrigin
@RestController
public class CrewController {
	@Autowired
	private CrewService crewService;
	
	
	
	
	// 크루 리스트 조회
	@GetMapping("/crews")
	public ResponseEntity<ArrayList<Crew>> selectCrewList(){
		
		ArrayList<Crew> list = crewService.selectCrewList();
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	
	
	// 크루 등록
	
	// 크루 수정
	
	// 크루 삭제
	
	// 크루 검색
	
	
	
	
	
	// 댓글 조회
	
	// 댓글 작성
	
	// 댓글 삭제
	
	
	
	
	
	
	
	// 크루 신청버튼 클릭
	
	// 크루 탈퇴
	
	

}
