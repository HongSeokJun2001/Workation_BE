package com.kh.workation.notice.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.workation.notice.model.dao.NoticeDao;
import com.kh.workation.notice.model.vo.Notice;


@Service
public class NoticeServiceImpl implements NoticeService{
	
	@Autowired
	private NoticeDao noticeDao;

	@Override
	public List<Notice> selectNoticeList() {
		return noticeDao.findAll();
	}

	@Override
	public Notice selectNoticeDetail(int noticeId) {
		return null;
	}

	@Override
	public Notice insertNotice(Notice n) {
		return null;
	}

	@Override
	public Notice updateNotice(Notice n) {
		return null;
	}

	@Override
	public int deleteNotice(int noticeId) {
		return 0;
	}
	
	

}
