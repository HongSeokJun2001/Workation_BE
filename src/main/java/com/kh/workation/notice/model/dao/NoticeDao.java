package com.kh.workation.notice.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.notice.model.vo.Notice;

public interface NoticeDao extends JpaRepository<Notice,Integer>{
	
	

}
