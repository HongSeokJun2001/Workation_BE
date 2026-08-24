package com.kh.workation.facility.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.workation.facility.model.vo.Facility;

@Repository
public interface FacilityDao extends JpaRepository<Facility, Long> {

	// 1. 전체 시설 페이징 조회 (imageList를 한번에 패치 조인하여 N+1 문제 방지)
	@Override
	@EntityGraph(attributePaths = {"imageList"})
	Page<Facility> findAll(Pageable pageable);
	
	// 2. 시설 검색 페이징 조회 (시설명 또는 지역 검색 + imageList 패치 조인)
	@EntityGraph(attributePaths = {"imageList"})
	Page<Facility> findByFacilityNameContainingOrRegionContaining(String facilityName, String region, Pageable pageable);
}
