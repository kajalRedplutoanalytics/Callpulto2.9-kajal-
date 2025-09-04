package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_validation_output")
public class QaValidationOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trading_order_details_id")
    private TradingOrderDetails tradingOrder;

    @ManyToOne
    @JoinColumn(name = "call_order_details_id")
    private CallOrderDetails callOrder;

    @Column(name = "rm_name")
    private String rmName;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "instrument_name")
    private String instrumentName;
    
    @Column(name = "call_type_from_recording")
    private String callTypeFromRecording;

    @Column(name = "call_type_from_trading_order")
    private String callTypeFromTradingOrder;

    @Column(name = "transaction_type_from_recording")
    private String transactionTypeFromRecording;

    @Column(name = "transaction_type_from_trading_order")
    private String transactionTypeFromTradingOrder;

//    @Column(name = "call_type")
//    private String callType;
//
//    @Column(name = "transaction_type")
//    private String transactionType;

    public String getCallTypeFromRecording() {
		return callTypeFromRecording;
	}

	public void setCallTypeFromRecording(String callTypeFromRecording) {
		this.callTypeFromRecording = callTypeFromRecording;
	}

	public String getCallTypeFromTradingOrder() {
		return callTypeFromTradingOrder;
	}

	public void setCallTypeFromTradingOrder(String callTypeFromTradingOrder) {
		this.callTypeFromTradingOrder = callTypeFromTradingOrder;
	}

	public String getTransactionTypeFromRecording() {
		return transactionTypeFromRecording;
	}

	public void setTransactionTypeFromRecording(String transactionTypeFromRecording) {
		this.transactionTypeFromRecording = transactionTypeFromRecording;
	}

	public String getTransactionTypeFromTradingOrder() {
		return transactionTypeFromTradingOrder;
	}

	public void setTransactionTypeFromTradingOrder(String transactionTypeFromTradingOrder) {
		this.transactionTypeFromTradingOrder = transactionTypeFromTradingOrder;
	}

	@Column(name = "call_order_quantity")
    private String callOrderQuantity;

    @Column(name = "trading_order_quantity")
    private String tradingOrderQuantity;

    @Column(name = "call_order_price")
    private Double callOrderPrice;

    @Column(name = "trading_order_price")
    private Double tradingOrderPrice;

    @Column(name = "order_placed_time")
    private LocalDateTime orderPlacedTime;

    @Column(name = "order_execution_time")
    private LocalDateTime orderExecutionTime;

    @Column(name = "qty_difference")
    private Long qtyDifference;

    @Column(name = "qty_threshold")
    private Double qtyThreshold;

    @Column(name = "qty_qa_status")
    private String qtyQaStatus;

    @Column(name = "qty_mismatch_reason_id")
    private Long qtyMismatchReasonId;

    @Column(name = "price_difference")
    private Double priceDifference;

    @Column(name = "price_threshold")
    private Double priceThreshold;

    @Column(name = "price_qa_status")
    private String priceQaStatus;

    @Column(name = "price_mismatch_reason_id")
    private Long priceMismatchReasonId;

    @Column(name = "time_difference")
    private Duration timeDifference;

    @Column(name = "time_threshold")
    private Integer timeThreshold;

    @Column(name = "time_qa_status")
    private String timeQaStatus;

    @Column(name = "time_mismatch_reason_id")
    private Long timeMismatchReasonId;

    @Column(name = "final_qa_status")
    private String finalQaStatus;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TradingOrderDetails getTradingOrder() {
		return tradingOrder;
	}

	public void setTradingOrder(TradingOrderDetails tradingOrder) {
		this.tradingOrder = tradingOrder;
	}

	public CallOrderDetails getCallOrder() {
		return callOrder;
	}

	public void setCallOrder(CallOrderDetails callOrder) {
		this.callOrder = callOrder;
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

	

	public String getCallOrderQuantity() {
		return callOrderQuantity;
	}

	public void setCallOrderQuantity(String callOrderQuantity) {
		this.callOrderQuantity = callOrderQuantity;
	}

	public String getTradingOrderQuantity() {
		return tradingOrderQuantity;
	}

	public void setTradingOrderQuantity(String tradingOrderQuantity) {
		this.tradingOrderQuantity = tradingOrderQuantity;
	}

	public Double getCallOrderPrice() {
		return callOrderPrice;
	}

	public void setCallOrderPrice(Double callOrderPrice) {
		this.callOrderPrice = callOrderPrice;
	}

	public Double getTradingOrderPrice() {
		return tradingOrderPrice;
	}

	public void setTradingOrderPrice(Double tradingOrderPrice) {
		this.tradingOrderPrice = tradingOrderPrice;
	}

	public LocalDateTime getOrderPlacedTime() {
		return orderPlacedTime;
	}

	public void setOrderPlacedTime(LocalDateTime orderPlacedTime) {
		this.orderPlacedTime = orderPlacedTime;
	}

	public LocalDateTime getOrderExecutionTime() {
		return orderExecutionTime;
	}

	public void setOrderExecutionTime(LocalDateTime orderExecutionTime) {
		this.orderExecutionTime = orderExecutionTime;
	}

	public Long getQtyDifference() {
		return qtyDifference;
	}

	public void setQtyDifference(Long qtyDifference) {
		this.qtyDifference = qtyDifference;
	}

	public Double getQtyThreshold() {
		return qtyThreshold;
	}

	public void setQtyThreshold(Double qtyThreshold) {
		this.qtyThreshold = qtyThreshold;
	}

	public String getQtyQaStatus() {
		return qtyQaStatus;
	}

	public void setQtyQaStatus(String qtyQaStatus) {
		this.qtyQaStatus = qtyQaStatus;
	}

	public Long getQtyMismatchReasonId() {
		return qtyMismatchReasonId;
	}

	public void setQtyMismatchReasonId(Long qtyMismatchReasonId) {
		this.qtyMismatchReasonId = qtyMismatchReasonId;
	}

	public Double getPriceDifference() {
		return priceDifference;
	}

	public void setPriceDifference(Double priceDifference) {
		this.priceDifference = priceDifference;
	}

	public Double getPriceThreshold() {
		return priceThreshold;
	}

	public void setPriceThreshold(Double priceThreshold) {
		this.priceThreshold = priceThreshold;
	}

	public String getPriceQaStatus() {
		return priceQaStatus;
	}

	public void setPriceQaStatus(String priceQaStatus) {
		this.priceQaStatus = priceQaStatus;
	}

	public Long getPriceMismatchReasonId() {
		return priceMismatchReasonId;
	}

	public void setPriceMismatchReasonId(Long priceMismatchReasonId) {
		this.priceMismatchReasonId = priceMismatchReasonId;
	}

	public Duration getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(Duration timeDifference) {
		this.timeDifference = timeDifference;
	}

	public Integer getTimeThreshold() {
		return timeThreshold;
	}

	public void setTimeThreshold(Integer timeThreshold) {
		this.timeThreshold = timeThreshold;
	}

	public String getTimeQaStatus() {
		return timeQaStatus;
	}

	public void setTimeQaStatus(String timeQaStatus) {
		this.timeQaStatus = timeQaStatus;
	}

	public Long getTimeMismatchReasonId() {
		return timeMismatchReasonId;
	}

	public void setTimeMismatchReasonId(Long timeMismatchReasonId) {
		this.timeMismatchReasonId = timeMismatchReasonId;
	}

	public String getFinalQaStatus() {
		return finalQaStatus;
	}

	public void setFinalQaStatus(String finalQaStatus) {
		this.finalQaStatus = finalQaStatus;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}


}
