// package com.kh.workation.member;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.persistence.autoconfigure.EntityScan;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// import com.kh.workation.auth.model.dao.AdminAuthDao;
// import com.kh.workation.auth.model.dao.AuthDao;
// import com.kh.workation.auth.model.dao.CompanyAuthDao;
// import com.kh.workation.facility.model.dao.FacilityDao;
// import com.kh.workation.facility.model.dao.FacilityImageDao;
// import com.kh.workation.facility.model.vo.Facility;
// import com.kh.workation.facility.model.vo.FacilityImage;
// import com.kh.workation.member.model.dao.AdminDao;
// import com.kh.workation.member.model.dao.CompanyDao;
// import com.kh.workation.member.model.dao.EmployeeDao;
// import com.kh.workation.member.model.vo.Admin;
// import com.kh.workation.member.model.vo.Company;
// import com.kh.workation.member.model.vo.Employee;

// @SpringBootApplication(scanBasePackages = {
//         "com.kh.workation.auth",
//         "com.kh.workation.common",
//         "com.kh.workation.config",
//         "com.kh.workation.member",
//         "com.kh.workation.facility"
// })
// @EntityScan(basePackageClasses = {Admin.class, Company.class, Employee.class, Facility.class, FacilityImage.class})
// @EnableJpaRepositories(basePackageClasses = {
//         AdminAuthDao.class,
//         AuthDao.class,
//         CompanyAuthDao.class,
//         AdminDao.class,
//         EmployeeDao.class,
//         CompanyDao.class,
//         FacilityDao.class,
//         FacilityImageDao.class
// })
// public class MemberDevApplication {

//     public static void main(String[] args) {
//         SpringApplication.run(MemberDevApplication.class, args);
//     }
// }
