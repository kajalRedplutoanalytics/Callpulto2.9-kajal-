package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mismatch_threshold")
public class MismatchThreshold {

    @Id
    @Column(name = "threshold_id")
    private Integer thresholdId;

    @ManyToOne
    @JoinColumn(name = "product_details_id", referencedColumnName = "product_details_id")
    private ProductDetails product;

    @Column(name = "price_threshold")
    private BigDecimal priceThreshold;

    @Column(name = "quantity_threshold")
    private BigDecimal quantityThreshold;

    @Column(name = "time_threshold")
    private Integer timeThreshold;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public Integer getThresholdId() {
		return thresholdId;
	}

	public void setThresholdId(Integer thresholdId) {
		this.thresholdId = thresholdId;
	}

	public ProductDetails getProduct() {
		return product;
	}

	public void setProduct(ProductDetails product) {
		this.product = product;
	}

	public BigDecimal getPriceThreshold() {
		return priceThreshold;
	}

	public void setPriceThreshold(BigDecimal priceThreshold) {
		this.priceThreshold = priceThreshold;
	}

	public BigDecimal getQuantityThreshold() {
		return quantityThreshold;
	}

	public void setQuantityThreshold(BigDecimal quantityThreshold) {
		this.quantityThreshold = quantityThreshold;
	}

	public Integer getTimeThreshold() {
		return timeThreshold;
	}

	public void setTimeThreshold(Integer timeThreshold) {
		this.timeThreshold = timeThreshold;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

    
}
