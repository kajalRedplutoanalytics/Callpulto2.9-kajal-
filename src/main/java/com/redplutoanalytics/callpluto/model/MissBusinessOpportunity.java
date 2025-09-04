package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "miss_business_opportunity")
public class MissBusinessOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mob_id")
    private Long mobId;

    @Column(name = "recording_id")
    private String recordingId;

    @Column(name = "mbo_status")
    private String mboStatus;

    @Column(name = "mbo_summary")
    private String mboSummary;

    @Column(name = "opportunity_type")
    private String opportunityType;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public Long getMobId() {
		return mobId;
	}

	public void setMobId(Long mobId) {
		this.mobId = mobId;
	}

	public String getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(String recordingId) {
		this.recordingId = recordingId;
	}

	public String getMboStatus() {
		return mboStatus;
	}

	public void setMboStatus(String mboStatus) {
		this.mboStatus = mboStatus;
	}

	public String getMboSummary() {
		return mboSummary;
	}

	public void setMboSummary(String mboSummary) {
		this.mboSummary = mboSummary;
	}

	public String getOpportunityType() {
		return opportunityType;
	}

	public void setOpportunityType(String opportunityType) {
		this.opportunityType = opportunityType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}


}
