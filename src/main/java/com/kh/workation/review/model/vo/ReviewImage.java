package com.kh.workation.review.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.ToString;

@Entity
@Table(name = "REVIEW_IMAGE")

@DynamicInsert
@DynamicUpdate

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "review")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", nullable = false)
    @JsonIgnore
    private Review review;

    @Column(name = "ORIGINAL_NAME", length = 255, nullable = false)
    private String originalName;

    @Column(name = "CHANGED_NAME", length = 255, nullable = false)
    private String changedName;

    @Column(name = "FILE_PATH", length = 900, nullable = false)
    private String filePath;

    @CreationTimestamp
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;
}