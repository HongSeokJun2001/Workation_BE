package com.kh.workation.facility.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kh.workation.facility.model.vo.Facility;

@Repository
public interface FacilityDao extends JpaRepository<Facility, Long> {

    // ================= [일반 사용자 / 비로그인 전용 (ACTIVE)] =================

    // 1. ACTIVE 목록 - 최신순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE f.status = 'ACTIVE' AND (:region = 'ALL' OR f.region = :region) ORDER BY f.createdDate DESC")
    Page<Facility> findAllActiveFacilitiesDesc(@Param("region") String region, Pageable pageable);

    // 2. ACTIVE 목록 - 오래된순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE f.status = 'ACTIVE' AND (:region = 'ALL' OR f.region = :region) ORDER BY f.createdDate ASC")
    Page<Facility> findAllActiveFacilitiesAsc(@Param("region") String region, Pageable pageable);

    @Query("SELECT COUNT(f) FROM Facility f WHERE f.status = 'ACTIVE'")
    long countActiveFacilities();

    // 3. ACTIVE 검색 - 최신순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE f.status = 'ACTIVE' AND (:region = 'ALL' OR f.region = :region) AND (f.facilityName LIKE %:keyword% OR f.region LIKE %:keyword%) ORDER BY f.createdDate DESC")
    Page<Facility> searchActiveFacilitiesDesc(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);

    // 4. ACTIVE 검색 - 오래된순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE f.status = 'ACTIVE' AND (:region = 'ALL' OR f.region = :region) AND (f.facilityName LIKE %:keyword% OR f.region LIKE %:keyword%) ORDER BY f.createdDate ASC")
    Page<Facility> searchActiveFacilitiesAsc(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);


    // ================= [최고관리자 전용 (전체 상태)] =================

    // 5. 전체 목록 - 최신순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE (:region = 'ALL' OR f.region = :region) ORDER BY f.createdDate DESC")
    Page<Facility> findAllFacilitiesForAdminDesc(@Param("region") String region, Pageable pageable);

    // 6. 전체 목록 - 오래된순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE (:region = 'ALL' OR f.region = :region) ORDER BY f.createdDate ASC")
    Page<Facility> findAllFacilitiesForAdminAsc(@Param("region") String region, Pageable pageable);

    // 7. 전체 검색 - 최신순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE (:region = 'ALL' OR f.region = :region) AND (f.facilityName LIKE %:keyword% OR f.region LIKE %:keyword%) ORDER BY f.createdDate DESC")
    Page<Facility> searchFacilitiesForAdminDesc(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);

    // 8. 전체 검색 - 오래된순
    @EntityGraph(attributePaths = {"imageList"})
    @Query("SELECT f FROM Facility f WHERE (:region = 'ALL' OR f.region = :region) AND (f.facilityName LIKE %:keyword% OR f.region LIKE %:keyword%) ORDER BY f.createdDate ASC")
    Page<Facility> searchFacilitiesForAdminAsc(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);
}