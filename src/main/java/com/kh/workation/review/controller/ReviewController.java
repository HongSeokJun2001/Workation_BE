package com.kh.workation.review.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.review.model.service.ReviewService;
import com.kh.workation.review.model.vo.Review;

@CrossOrigin
@RestController
public class ReviewController {
	
	@Value("${file.upload-dir}")
	private String uploadDir;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;


    // Authorization 헤더에서 JWT 추출
    private String getToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        return authService.isValidToken(token) ? token : null;
    }


    // ----------------------------------------------------
    // 시설별 리뷰 목록 조회
    // GET /facilities/{facilityId}/reviews
    // ----------------------------------------------------
    @GetMapping("/facilities/{facilityId}/reviews")
    public ResponseEntity<List<Map<String, Object>>> selectReviewList(
            @PathVariable("facilityId") Long facilityId) {

        List<Map<String, Object>> response = reviewService
                .selectReviewList(facilityId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    // ----------------------------------------------------
    // 리뷰 작성
    // POST /facilities/{facilityId}/reviews
    // ----------------------------------------------------
    @PostMapping("/facilities/{facilityId}/reviews")
    public ResponseEntity<?> insertReview(
            @PathVariable("facilityId") Long facilityId,
            @ModelAttribute Review review,
            @RequestParam(value = "upfiles", required = false) MultipartFile[] upfiles,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
    	
        String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        // 리뷰 작성은 직원만 가능
        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("직원만 리뷰를 작성할 수 있습니다.");
        }

        String loginId = authService.getLoginId(token);

        try {

        	Review savedReview =
        	        reviewService.insertReview(
        	                facilityId,
        	                loginId,
        	                review,
        	                upfiles,
        	                uploadDir
        	        );

            return ResponseEntity.ok(toResponse(savedReview));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ----------------------------------------------------
    // 리뷰 수정
    // PUT /reviews/{reviewId}
    // ----------------------------------------------------
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody Review review,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        String loginId = authService.getLoginId(token);

        try {

        	Review updatedReview =
        	        reviewService.updateReview(
        	                reviewId,
        	                loginId,
        	                review
        	        );

            return ResponseEntity.ok(toResponse(updatedReview));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }


    // ----------------------------------------------------
    // 리뷰 삭제
    // DELETE /reviews/{reviewId}
    // ----------------------------------------------------
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        String loginId = authService.getLoginId(token);
        boolean isSuperAdmin = authService.isSuperAdminToken(token);
        boolean isCompanyAdmin = authService.isCompanyAdminToken(token);
        Long companyId = authService.getCompanyId(token);

        try {

            int result =
            		reviewService.deleteReview(
            		        reviewId,
            		        loginId,
            		        isSuperAdmin,
            		        isCompanyAdmin,
            		        companyId
            		);
            
            return ResponseEntity.ok(
                    result > 0 ? "success" : "fail"
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }


    // ----------------------------------------------------
    // Review Entity → 프론트 응답 데이터 변환
    // ----------------------------------------------------
    private Map<String, Object> toResponse(Review review) {

        Map<String, Object> response = new HashMap<>();

        response.put("reviewId", review.getReviewId());
        response.put("employeeId", review.getEmployee().getEmployeeId());
        response.put("loginId", review.getEmployee().getLoginId());
        response.put("companyId", review.getEmployee().getCompanyId());
        response.put("employeeName", review.getEmployee().getEmployeeName());
        response.put("rating", review.getRating());
        response.put("content", review.getContent());

        response.put(
                "images",
                review.getImageList()
                        .stream()
                        .map(image -> image.getFilePath())
                        .toList()
        );

        response.put("createdDate", review.getCreatedDate());
        response.put("updatedDate", review.getUpdatedDate());

        return response;
    }
}