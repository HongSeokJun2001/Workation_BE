package com.kh.workation.crew.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;
import com.kh.workation.reply.model.vo.Reply;

public interface CrewService {
	
	// 크루 리스트 조회
	Page<Crew> selectCrewList(Pageable pageable);
	
	// 크루 단건 조회
	Crew selectCrew(int crewId);
	
	// 크루 검색
	Page<Crew> searchCrewList(String keyword, Pageable pageable);
	
	// 크루 등록
	Crew insertCrew(Crew c);
	
	// 크루 수정
	
	Crew updateCrew(Crew c);
	
	// 크루삭제
	int deleteCrew(int crewId);
	
	// 크루 신청
	CrewMemberHist joinCrew(CrewMemberHist cm);

	// 내가 신청한 크루 조회
	List<CrewMemberHist> selectMyCrewList(Long employeeId);

	// 크루 탈퇴
	int leaveCrew(Long employeeId, int crewId);
	
	
	

}
