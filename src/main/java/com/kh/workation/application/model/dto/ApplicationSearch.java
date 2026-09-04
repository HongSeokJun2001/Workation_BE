package com.kh.workation.application.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationSearch {
    private String keyword;  
    private String status;   
    private String facilityId; 
}