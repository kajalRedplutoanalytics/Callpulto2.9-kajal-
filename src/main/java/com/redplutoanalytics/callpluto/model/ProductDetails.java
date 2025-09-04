package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_details")
public class ProductDetails {

    @Id
    @Column(name = "product_details_id")
    private String productDetailsId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "average_revenue_per_transaction")
    private BigDecimal averageRevenuePerTransaction;

    @Column(name = "average_commission")
    private BigDecimal averageCommission;

    @Column(name = "created_date")
    private LocalDate createdDate;

	public String getProductDetailsId() {
		return productDetailsId;
	}

	public void setProductDetailsId(String productDetailsId) {
		this.productDetailsId = productDetailsId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getAverageRevenuePerTransaction() {
		return averageRevenuePerTransaction;
	}

	public void setAverageRevenuePerTransaction(BigDecimal averageRevenuePerTransaction) {
		this.averageRevenuePerTransaction = averageRevenuePerTransaction;
	}

	public BigDecimal getAverageCommission() {
		return averageCommission;
	}

	public void setAverageCommission(BigDecimal averageCommission) {
		this.averageCommission = averageCommission;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}


}
