package com.kh.workation.reply.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.member.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="REPLY")

@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reply { // 댓글 수정은 없는지 ?
	
	@Id
	@Column(name="REPLY_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int replyId;
	
	
	@JoinColumn(name="CREW_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Crew crew;
	
	
	@JoinColumn(name="EMPLOYEE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee;
	
	
	@JoinColumn(name="REPLY")
	@ManyToOne(fetch = FetchType.LAZY)
	private int parentReplyNo;
	
	@Column(name="REPLY_CONTENT", nullable=false, length=500)
	private String replyContent;
	
	@Column(name="REPLY_PRIVATE",columnDefinition="VARCHAR2(20) DEFAULT 'Y'")
	private String replyPrivate;
	
	@Column(name="CREATE_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP" )
	private LocalDateTime createDate;
	
	@Column(name="STATUS", nullable=false, columnDefinition="DEFAULT 'Y'")
	private String status;
	

}
