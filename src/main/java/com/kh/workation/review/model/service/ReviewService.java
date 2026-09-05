package com.kh.workation.review.model.service;

import java.util.List;

import com.kh.workation.review.model.vo.Review;

public interface ReviewService {

    // 시설별 리뷰 목록 조회 (최신순)
    List<Review> selectReviewList(Long facilityId);

    // 리뷰 작성
    Review insertReview(Long facilityId, String loginId, Review review);

    // 리뷰 수정
    Review updateReview(Long reviewId, String loginId, Review review, String role);

    // 리뷰 삭제
    int deleteReview(Long reviewId, String loginId, String role);
}