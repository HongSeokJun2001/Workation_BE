package com.kh.workation.facility.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.workation.facility.model.vo.Facility;

@Repository
public interface FacilityDao extends JpaRepository<Facility, Integer> {

}
