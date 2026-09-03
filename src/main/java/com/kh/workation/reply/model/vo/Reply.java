package com.kh.workation.reply.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@ToString(exclude = {"crew", "employee", "parentReply"})
public class Reply { // 댓글 수정은 없는지 ?
	//	순환참조문제로 ㅅToString 제한
	
	@Id
	@Column(name="REPLY_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer replyId;
	
	
	@JoinColumn(name="CREW_ID", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Crew crew;
	
	
	@JoinColumn(name="EMPLOYEE_ID",nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee;
	
	
	// 부모 댓글
    // 일반 댓글이면 NULL
    // 대댓글이면 부모 댓글의 REPLY_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_REPLY_ID")
	@JsonIgnoreProperties("parentReply")
    private Reply parentReply;
    
    
	
	@Column(name="REPLY_CONTENT", nullable=false, length=700)
	private String replyContent;
	
	@Column(name="REPLY_PRIVATE",columnDefinition="VARCHAR(20) DEFAULT N'")
	private String replyPrivate;
	
	@Column(name="CREATED_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP" )
	private LocalDateTime createdDate;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'NORMAL'")
	private String status;
	

}
