package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equity_details")
public class EquityDetails {

    @Id
    @Column(name = "instrument_id")
    private String instrumentId;

    @Column(name = "instrument_name")
    private String instrumentName;

    @Column(name = "isin")
    private String isin;

    @Column(name = "nse_code")
    private String nseCode;

    @Column(name = "bse_code")
    private String bseCode;

    @ManyToOne
    @JoinColumn(name = "product_details_id", referencedColumnName = "product_details_id")
    private ProductDetails product;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getNseCode() {
		return nseCode;
	}

	public void setNseCode(String nseCode) {
		this.nseCode = nseCode;
	}

	public String getBseCode() {
		return bseCode;
	}

	public void setBseCode(String bseCode) {
		this.bseCode = bseCode;
	}

	public ProductDetails getProduct() {
		return product;
	}

	public void setProduct(ProductDetails product) {
		this.product = product;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

   
}
