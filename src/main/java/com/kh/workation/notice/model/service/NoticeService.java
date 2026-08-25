package com.kh.workation.notice.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.workation.notice.model.vo.Notice;

public interface NoticeService {
	
	
	Page<Notice> selectNoticeList(Pageable pageable);
	
	Notice selectNotice(int noticeId);
	
	int increaseCount(int noticeId);
	
	Notice insertNotice(Notice n);
	
	Notice updateNotice(Notice n);
	
	int deleteNotice(int noticeId);
	
	
	
	

}
