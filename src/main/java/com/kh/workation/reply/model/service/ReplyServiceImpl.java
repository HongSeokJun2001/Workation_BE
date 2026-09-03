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
	public Reply insertReply(Reply r, int crewId, String loginId) {
		Employee employee = employeeDao.findByLoginIdAndStatus(loginId, Employee.STATUS_ACTIVE)
				.orElseThrow(() -> new IllegalArgumentException("활성 직원이 아닙니다."));
		Crew crew = crewDao.findById(crewId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다."));

		r.setCrew(crew);
		r.setEmployee(employee);
		if (r.getParentReply() != null && r.getParentReply().getReplyId() != null) {
			Reply parentReply = replyDao.findById(r.getParentReply().getReplyId())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
			if (!parentReply.getCrew().getCrewId().equals(crewId)) {
				throw new IllegalArgumentException("다른 크루의 댓글에는 답글을 작성할 수 없습니다.");
			}
			r.setParentReply(parentReply);
		} else {
			r.setParentReply(null);
		}
		r.setReplyPrivate("Y".equals(r.getReplyPrivate()) ? "Y" : "N");
		r.setStatus("NORMAL");
		r.setCreatedDate(java.time.LocalDateTime.now());
		return replyDao.save(r);
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
