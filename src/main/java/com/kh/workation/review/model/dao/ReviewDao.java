package com.kh.workation.review.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.workation.review.model.vo.Review;

public interface ReviewDao extends JpaRepository<Review, Long> {

    List<Review> findByFacilityFacilityIdOrderByCreatedDateDesc(Long facilityId);
}