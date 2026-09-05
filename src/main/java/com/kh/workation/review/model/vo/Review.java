package com.kh.workation.review.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.facility.model.vo.Facility;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "REVIEW")

@DynamicInsert
@DynamicUpdate

@Getter
@Setter
@NoArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REVIEW_ID")
	private Long reviewId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FACILITY_ID", nullable = false)
	private Facility facility;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMPLOYEE_ID", nullable = false)
	private Employee employee;

	@Column(name = "RATING", nullable = false)
	private Integer rating;

	@Column(name = "CONTENT", length = 1000, nullable = false)
	private String content;

	@Column(
		name = "CREATED_DATE",
		updatable = false,
		columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
		nullable = false
	)
	private LocalDateTime createdDate;

	@Column(
		name = "UPDATED_DATE",
		columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
		nullable = false
	)
	private LocalDateTime updatedDate;
}