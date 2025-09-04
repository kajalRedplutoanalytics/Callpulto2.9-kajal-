package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_records")
public class OrderRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "trading_order_details_id")
    private String tradingOrderDetailsId;

    @Column(name = "call_order_details_id")
    private String callOrderDetailsId;

    @Column(name = "rm_name")
    private String rmName;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "instrument_name")
    private String instrumentName;

    @Column(name = "call_order_quantity")
    private BigDecimal callOrderQuantity;

    @Column(name = "trading_order_quantity")
    private BigDecimal tradingOrderQuantity;

    @Column(name = "call_order_price")
    private BigDecimal callOrderPrice;

    @Column(name = "trading_order_price")
    private BigDecimal tradingOrderPrice;

    @Column(name = "is_match")
    private Boolean isMatch;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTradingOrderDetailsId() {
		return tradingOrderDetailsId;
	}

	public void setTradingOrderDetailsId(String tradingOrderDetailsId) {
		this.tradingOrderDetailsId = tradingOrderDetailsId;
	}

	public String getCallOrderDetailsId() {
		return callOrderDetailsId;
	}

	public void setCallOrderDetailsId(String callOrderDetailsId) {
		this.callOrderDetailsId = callOrderDetailsId;
	}

	public String getRmName() {
		return rmName;
	}

	public void setRmName(String rmName) {
		this.rmName = rmName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public BigDecimal getCallOrderQuantity() {
		return callOrderQuantity;
	}

	public void setCallOrderQuantity(BigDecimal callOrderQuantity) {
		this.callOrderQuantity = callOrderQuantity;
	}

	public BigDecimal getTradingOrderQuantity() {
		return tradingOrderQuantity;
	}

	public void setTradingOrderQuantity(BigDecimal tradingOrderQuantity) {
		this.tradingOrderQuantity = tradingOrderQuantity;
	}

	public BigDecimal getCallOrderPrice() {
		return callOrderPrice;
	}

	public void setCallOrderPrice(BigDecimal callOrderPrice) {
		this.callOrderPrice = callOrderPrice;
	}

	public BigDecimal getTradingOrderPrice() {
		return tradingOrderPrice;
	}

	public void setTradingOrderPrice(BigDecimal tradingOrderPrice) {
		this.tradingOrderPrice = tradingOrderPrice;
	}

	public Boolean getIsMatch() {
		return isMatch;
	}

	public void setIsMatch(Boolean isMatch) {
		this.isMatch = isMatch;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
