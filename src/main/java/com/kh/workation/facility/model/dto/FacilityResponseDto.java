package com.kh.workation.facility.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.kh.workation.facility.model.vo.Facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FacilityResponseDto {

	private int facilityId;
	private String facilityType;
	private String facilityName;
	private String region;
	private String address;
	private String description;
	private String status;
	private int roomCount;
	private int maxPerson;
	private LocalDateTime createdDate;
	
	// 이미지 파일 경로 목록
	private List<String> imagePaths;
	
	// Entity -> DTO 변환 생성자/메서드
	public static FacilityResponseDto fromEntity(Facility facility) {
		List<String> paths = null;
		if(facility.getImageList() != null) {
			paths = facility.getImageList().stream()
					.map(img -> img.getFilePath())
					.collect(Collectors.toList());
		}
		
		return FacilityResponseDto.builder()
				.facilityId(facility.getFacilityId())
				.facilityType(facility.getFacilityType())
				.facilityName(facility.getFacilityName())
				.region(facility.getRegion())
				.address(facility.getAddress())
				.description(facility.getDescription())
				.status(facility.getStatus())
				.roomCount(facility.getRoomCount())
				.maxPerson(facility.getMaxPerson())
				.createdDate(facility.getCreatedDate())
				.imagePaths(paths)
				.build();
	}
	
}
