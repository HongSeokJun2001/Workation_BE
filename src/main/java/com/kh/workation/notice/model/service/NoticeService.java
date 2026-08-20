package com.kh.workation.notice.model.service;

import java.util.List;

import com.kh.workation.notice.model.vo.Notice;

public interface NoticeService {
	
	
	List<Notice> selectNoticeList();
	
	Notice selectNoticeDetail(int noticeId);
	
	Notice insertNotice(Notice n);
	
	Notice updateNotice(Notice n);
	
	int deleteNotice(int noticeId);
	
	
	
	

}
