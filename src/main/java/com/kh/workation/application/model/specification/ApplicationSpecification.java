package com.kh.workation.application.model.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.kh.workation.application.model.dto.ApplicationSearch;
import com.kh.workation.application.model.vo.Application;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ApplicationSpecification {

    public static Specification<Application> searchWith(ApplicationSearch searchDto, Long companyId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 기업 ID 조건
            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("companyId"), companyId));
            }

            // 2. 검색어 (크루명 또는 크루장 이름)
            if (searchDto != null && searchDto.getKeyword() != null && !searchDto.getKeyword().trim().isEmpty()) {
                String kw = "%" + searchDto.getKeyword().trim() + "%";
                Predicate crewNameLike = cb.like(root.get("crew").get("crewName"), kw);
                Predicate leaderNameLike = cb.like(root.get("crew").get("employee").get("employeeName"), kw);
                predicates.add(cb.or(crewNameLike, leaderNameLike));
            }

            // 3. 상태 필터 (ALL이 아닌 경우)
            if (searchDto != null && searchDto.getStatus() != null && !"ALL".equalsIgnoreCase(searchDto.getStatus())) {
                predicates.add(cb.equal(root.get("progress").get("status"), searchDto.getStatus()));
            }

            // 4. 시설 필터 (ALL이 아닌 경우)
            if (searchDto != null && searchDto.getFacilityId() != null && !"ALL".equalsIgnoreCase(searchDto.getFacilityId())) {
                try {
                    Long facilityId = Long.parseLong(searchDto.getFacilityId());
                    predicates.add(cb.equal(root.get("facility").get("facilityId"), facilityId));
                } catch (NumberFormatException e) {
                    // 숫자 변환 불가 시 무시
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<Application> searchWith(ApplicationSearch searchDto, String loginId) {
        return (root, query, cb) -> {
        	
        	query.distinct(true);
        	
            List<Predicate> predicates = new ArrayList<>();
            
            // 1. 로그인한 사용자가 속한 크루 조건 (Application -> Crew -> CrewMemberHist -> Employee)
            if (loginId != null && !loginId.trim().isEmpty()) {
                Join<Object, Object> crewJoin = root.join("crew", JoinType.LEFT);
                Join<Object, Object> memberHistJoin = crewJoin.join("crewMemberHists", JoinType.LEFT);
                Join<Object, Object> employeeJoin = memberHistJoin.join("employee", JoinType.LEFT);

                predicates.add(cb.equal(employeeJoin.get("loginId"), loginId));
            }

            // 2. 검색어 (크루명 또는 크루장 이름)
            if (searchDto != null && searchDto.getKeyword() != null && !searchDto.getKeyword().trim().isEmpty()) {
                String kw = "%" + searchDto.getKeyword().trim() + "%";
                Predicate crewNameLike = cb.like(root.get("crew").get("crewName"), kw);
                Predicate leaderNameLike = cb.like(root.get("crew").get("employee").get("employeeName"), kw);
                predicates.add(cb.or(crewNameLike, leaderNameLike));
            }

            // 3. 상태 필터 (ALL이 아닌 경우)
            if (searchDto != null && searchDto.getStatus() != null && !"ALL".equalsIgnoreCase(searchDto.getStatus())) {
                predicates.add(cb.equal(root.get("progress").get("status"), searchDto.getStatus()));
            }

            // 4. 시설 필터 (ALL이 아닌 경우)
            if (searchDto != null && searchDto.getFacilityId() != null && !"ALL".equalsIgnoreCase(searchDto.getFacilityId())) {
                try {
                    Long facilityId = Long.parseLong(searchDto.getFacilityId());
                    predicates.add(cb.equal(root.get("facility").get("facilityId"), facilityId));
                } catch (NumberFormatException e) {
                    // 숫자 변환 불가 시 무시
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}