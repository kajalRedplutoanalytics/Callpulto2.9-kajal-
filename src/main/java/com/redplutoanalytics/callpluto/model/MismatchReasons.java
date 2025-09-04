package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mismatch_reasons")
public class MismatchReasons {

    @Id
    @Column(name = "mismatch_reason_id")
    private Integer mismatchReasonId;

    @Column(name = "category")
    private String category;

    @Column(name = "reason")
    private String reason;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

	public Integer getMismatchReasonId() {
		return mismatchReasonId;
	}

	public void setMismatchReasonId(Integer mismatchReasonId) {
		this.mismatchReasonId = mismatchReasonId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

  
}
