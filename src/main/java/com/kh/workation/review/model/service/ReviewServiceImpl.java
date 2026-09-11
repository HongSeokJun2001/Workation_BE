package com.kh.workation.review.model.service;

import java.util.List;
import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.workation.facility.model.dao.FacilityDao;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.member.model.dao.EmployeeDao;
import com.kh.workation.member.model.vo.Employee;
import com.kh.workation.review.model.dao.ReviewDao;
import com.kh.workation.review.model.vo.Review;
import com.kh.workation.review.model.vo.ReviewImage;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private FacilityDao facilityDao;

    // 시설별 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<Review> selectReviewList(Long facilityId) {
        return reviewDao.findByFacilityFacilityIdOrderByCreatedDateDesc(facilityId);
    }

    // 리뷰 작성
    @Override
    @Transactional
    public Review insertReview(
            Long facilityId,
            String loginId,
            Review review,
            MultipartFile[] upfiles,
            String savePath) {

        Employee employee = employeeDao.findByLoginIdAndStatus(
                loginId,
                Employee.STATUS_ACTIVE
        ).orElseThrow(() ->
                new IllegalArgumentException("로그인한 직원 정보를 찾을 수 없습니다.")
        );

        Facility facility = facilityDao.findById(facilityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 시설을 찾을 수 없습니다.")
                );
        
        boolean alreadyReviewed =
                reviewDao.existsByFacilityFacilityIdAndEmployeeEmployeeId(
                        facilityId,
                        employee.getEmployeeId()
                );

        if (alreadyReviewed) {
            throw new IllegalArgumentException("이미 해당 시설에 리뷰를 작성했습니다.");
        }
        

        if (review.getRating() == null
                || review.getRating() < 1
                || review.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지 입력할 수 있습니다.");
        }

        if (review.getContent() == null
                || review.getContent().isBlank()) {
            throw new IllegalArgumentException("리뷰 내용을 입력해주세요.");
        }

        review.setEmployee(employee);
        review.setFacility(facility);

        // 리뷰 먼저 저장
        Review savedReview = reviewDao.save(review);

        // 첨부 이미지가 있을 경우 저장
        if (upfiles != null && upfiles.length > 0) {

            File dir = new File(System.getProperty("user.dir"), savePath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : upfiles) {

                if (file == null || file.isEmpty()) {
                    continue;
                }

                try {
                    String originName = file.getOriginalFilename();

                    String ext = originName.substring(originName.lastIndexOf("."));
                    String changeName = UUID.randomUUID().toString() + ext;

                    File destFile = new File(dir, changeName);

                    file.transferTo(destFile.getAbsoluteFile());

                    ReviewImage reviewImage = new ReviewImage();
                    reviewImage.setOriginalName(originName);
                    reviewImage.setChangedName(changeName);
                    reviewImage.setFilePath("/uploads/" + changeName);

                    savedReview.addImage(reviewImage);

                } catch (Exception e) {
                    throw new IllegalArgumentException("리뷰 이미지 저장 중 오류가 발생했습니다.");
                }
            }

            reviewDao.save(savedReview);
        }

        return savedReview;
    }

    // 리뷰 수정
    @Override
    @Transactional
    public Review updateReview(
            Long reviewId,
            String loginId,
            Review review) {

        Review existingReview = reviewDao.findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다.")
                );

        boolean isWriter =
                existingReview.getEmployee()
                        .getLoginId()
                        .equals(loginId);

        if (!isWriter) {
            throw new IllegalArgumentException("리뷰 수정 권한이 없습니다.");
        }

        if (review.getRating() == null
                || review.getRating() < 1
                || review.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지 입력할 수 있습니다.");
        }

        if (review.getContent() == null
                || review.getContent().isBlank()) {
            throw new IllegalArgumentException("리뷰 내용을 입력해주세요.");
        }

        existingReview.setRating(review.getRating());
        existingReview.setContent(review.getContent());

        return existingReview;
    }

    // 리뷰 삭제
    @Override
    @Transactional
    public int deleteReview(
            Long reviewId,
            String loginId,
            boolean isSuperAdmin,
            boolean isCompanyAdmin,
            Long companyId) {

        Review existingReview = reviewDao.findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다.")
                );

        boolean isWriter =
                existingReview.getEmployee()
                        .getLoginId()
                        .equals(loginId);
        
        boolean isSameCompanyAdmin =
                isCompanyAdmin
                && companyId != null
                && companyId.equals(
                        existingReview.getEmployee().getCompanyId()
                );

        if (!isWriter && !isSuperAdmin && !isSameCompanyAdmin) {
            throw new IllegalArgumentException("리뷰 삭제 권한이 없습니다.");
        }

        reviewDao.delete(existingReview);

        return 1;
    }
}