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
import io.swagger.v3.oas.annotations.media.Schema;

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
	@Schema(description = "공지사항 고유 번호", example = "1")
	private Integer noticeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ADMIN_ID")
	private Admin admin;
	
	@Column(name="NOTICE_TITLE", nullable=false, length=100)
	@Schema(description = "공지사항 제목", example = "9월 워케이션 안내")
	private String noticeTitle;
	
	@Column(name="NOTICE_CONTENT", length=1500)
	@Schema(description = "공지사항 내용", example = "신청 일정을 확인해 주세요.")
	private String noticeContent;
	
	@Column(name="VIEW_COUNT", nullable=false, columnDefinition="INT DEFAULT 0")
	@Schema(description = "조회수", example = "12")
	private Integer viewCount;
	
	@Column(name="STATUS", nullable=false, columnDefinition="VARCHAR(20) DEFAULT 'Y'")
	@Schema(description = "공지 상태", example = "Y", allowableValues = {"Y", "N"})
	private String status;
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Schema(description = "작성일시", example = "2026-09-08T10:00:00")
	private LocalDateTime createDate;
	
	@Column(name="UPDATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Schema(description = "수정일시", example = "2026-09-08T11:00:00")
	private LocalDateTime updateDate;
	
	
	

}
