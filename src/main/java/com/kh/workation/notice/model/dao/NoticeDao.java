package com.kh.workation.notice.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.notice.model.vo.Notice;

public interface NoticeDao extends JpaRepository<Notice,Integer>{

	Page<Notice> findByStatusOrderByNoticeIdDesc(String status, Pageable pageable);
	
	@Modifying
	@Query("""
			UPDATE Notice n
			  SET n.viewCount = n.viewCount + 1
			  WHERE n.noticeId = :noticeId
			  AND n.status = 'Y'			
			""")
	int increaseCount(@Param("noticeId")int noticeId);

	Notice findByNoticeIdAndStatus(int noticeId, String status);

	//공지사항 삭제용 JPQL(소프트 삭제)
	@Modifying
	@Query("""
			UPDATE Notice n
			  SET n.status = 'N'
			  WHERE n.noticeId = :noticeId
			  AND n.status = 'Y'
			""")
	int deleteNotice(@Param("noticeId")int noticeId);

	
	
	

}

