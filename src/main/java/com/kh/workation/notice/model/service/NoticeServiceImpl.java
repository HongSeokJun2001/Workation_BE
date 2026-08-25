package com.kh.workation.notice.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.notice.model.dao.NoticeDao;
import com.kh.workation.notice.model.vo.Notice;

@Service
public class NoticeServiceImpl implements NoticeService{
	
	@Autowired
	private NoticeDao noticeDao;

	@Override
	public Page<Notice> selectNoticeList(Pageable pageable) {
		return noticeDao.findByStatusOrderByNoticeIdDesc("Y",pageable);
	}
	
	@Transactional
	@Override
	public int increaseCount(int noticeId) {
		
		return noticeDao.increaseCount(noticeId);
		
	}
	
	@Transactional(readOnly = true)
	@Override
	public Notice selectNotice(int noticeId) {
		return noticeDao.findByNoticeIdAndStatus(noticeId, "Y");
	}

	@Override
	public Notice insertNotice(Notice n) {
		return noticeDao.save(n);
	}

	@Override
	public Notice updateNotice(Notice n) {
		return noticeDao.save(n);
	}
	
	@Transactional
	@Override
	public int deleteNotice(int noticeId) {
		return noticeDao.deleteNotice(noticeId);
	}
	
	

}
