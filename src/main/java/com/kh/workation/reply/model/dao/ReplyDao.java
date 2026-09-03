package com.kh.workation.reply.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.workation.reply.model.vo.Reply;

public interface ReplyDao extends JpaRepository<Reply, Integer>{

	
	
	// 댓글 삭제용 JPQL(소프트 삭)
	@Modifying
	@Query("""
			
			UPDATE Reply r
           SET r.status = 'DELETED'
         WHERE r.replyId = :replyId
           AND r.employee.loginId = :loginId
           AND r.status = 'NORMAL'
           
			""")
	int deleteReply(@Param("replyId") int replyId,@Param("loginId") String loginId);

	
	
	@Query("""
			SELECT r
          FROM Reply r
         WHERE r.crew.crewId = :crewId
           AND r.status = 'NORMAL'
         ORDER BY r.replyId ASC
			""")
	List<Reply> selectReplyList(@Param("crewId") int crewId);


	
	
}
