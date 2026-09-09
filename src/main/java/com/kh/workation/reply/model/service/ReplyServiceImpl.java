package com.kh.workation.reply.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.crew.model.dao.CrewDao;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.reply.model.dao.ReplyDao;
import com.kh.workation.reply.model.dto.ReplyCreateRequest;
import com.kh.workation.reply.model.vo.Reply;

@Service
public class ReplyServiceImpl implements ReplyService{
	
	@Autowired
	private ReplyDao replyDao;
	
	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CrewDao crewDao;

	@Override
	public List<Reply> selectReplyList(int crewId) {
		// TODO Auto-generated method stub
		//return replyDao.findByCrewIdContaningStatusOrderByCreatedDateDesc(crewId,"NORMAL");
		//return replyDao.findByCrewIdAndStatusOrderByCreatedDateDesc(crewId, "");
		
		return replyDao.selectReplyList(crewId);
	}

	@Transactional
	@Override
	public Reply insertReply(ReplyCreateRequest request, int crewId, String loginId) {
		Employee employee = employeeDao.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElseThrow(() -> new IllegalArgumentException("활성 직원이 아닙니다."));
		Crew crew = crewDao.findById(crewId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

		Reply reply = new Reply();
		reply.setReplyContent(request.getReplyContent());
		reply.setReplyPrivate(request.getReplyPrivate());
		reply.setCrew(crew);
		reply.setEmployee(employee);
		if (request.getParentReplyId() != null) {
			Reply parentReply = replyDao.findById(request.getParentReplyId())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
			if (!parentReply.getCrew().getCrewId().equals(crewId)) {
				throw new IllegalArgumentException("다른 크루의 댓글에는 답글을 작성할 수 없습니다.");
			}
			reply.setParentReply(parentReply);
		} else {
			reply.setParentReply(null);
		}
		reply.setReplyPrivate("Y".equals(request.getReplyPrivate()) ? "Y" : "N");
		reply.setStatus("NORMAL");
		reply.setCreatedDate(java.time.LocalDateTime.now());
		return replyDao.save(reply);
	}
	
	@Transactional
	@Override
	public int deleteReply(int replyId, String loginId) {
		// TODO Auto-generated method stub
		
//		Employee employee = employeeDao.findByLoginIdAndStatus(loginId, "ACTIVE")
//		        .orElse(null);
		
		
		return replyDao.deleteReply(replyId, loginId);
	}

}
