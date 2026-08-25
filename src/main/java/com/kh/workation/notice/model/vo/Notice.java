package com.kh.workation.notice.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.member.model.vo.Admin;

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

// Swagger 문서

@Entity
@Table(name="NOTICE")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Notice {
	
	@Id
	@Column(name="NOTICE_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer noticeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ADMIN_ID")
	private Admin admin;
	
	@Column(name="NOTICE_TITLE", nullable=false, length=100)
	private String noticeTitle;
	
	@Column(name="NOTICE_CONTENT", length=1500)
	private String noticeContent;
	
	@Column(name="VIEW_COUNT", nullable=false, columnDefinition="INT DEFAULT 0")
	private Integer viewCount;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'Y'")
	private String status;
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate;
	
	@Column(name="UPDATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime updateDate;
	
	
	

}
