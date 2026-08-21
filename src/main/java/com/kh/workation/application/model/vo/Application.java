package com.kh.workation.application.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.facility.model.vo.Facility;
import com.kh.workation.member.model.vo.Company;
import com.kh.workation.reservation.model.vo.ReservationDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Schema(description="워케이션 신청 엔티티")

@Entity
@Table(name="WORKATION_APPLICATION")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Application {

	@Id
	@Column(name = "WORKATION_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int workationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;
	
	@ManyToOne(fetch = FetchType.EAGER) // 또는 FetchType.LAZY + Fetch Join
    @JoinColumn(name = "CREW_ID", nullable = false)
    private Crew crew;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID")
    private Facility facility; 
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERV_DT_ID")
    private ReservationDate reservationDate;
	
	@Column(name = "REGION", length = 20)
	private String region;
	
	@Column(name = "PURPOSE", length = 900)
	private String purpose;
	
	@Column(name="CREATED_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdDate;
	
	@OneToOne
    @JoinColumn(name = "WORKATION_ID", insertable = false, updatable = false)
    private Progress progress;
	
	public String getStatus() {
        if (this.progress == null) {
            return "APPLY"; 
        }
        return this.progress.getStatus();
    }
	
}
