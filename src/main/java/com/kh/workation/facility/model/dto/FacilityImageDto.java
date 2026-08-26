package com.kh.workation.facility.model.dto;

import com.kh.workation.facility.model.vo.FacilityImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityImageDto {

	private Long imageId; 	 //이미지 ID
	private String filePath; // 이미지 파일 경로
	
	// Entity -> DTO 변환 생성자
	public FacilityImageDto(FacilityImage image) {
		this.imageId = image.getImageId();
		this.filePath = image.getFilePath();
	}
}
