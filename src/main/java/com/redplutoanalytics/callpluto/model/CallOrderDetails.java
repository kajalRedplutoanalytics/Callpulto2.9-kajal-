package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_order_details")
public class CallOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_order_details_id")
    private Long callOrderDetailsId;

    @Column(name = "recording_name")
    private String recordingName;

    @Column(name = "extracted_instrument_name")
    private String extractedInstrumentName;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "stop_loss")
    private BigDecimal stopLoss;

    @Column(name = "target_price")
    private BigDecimal targetPrice;

    @Column(name = "transcription")
    private String transcription;

    @Column(name = "translation")
    private String translation;

    @Column(name = "summary")
    private String summary;

    @Column(name = "order_placed_time")
    private LocalDateTime orderPlacedTime;

    @Column(name = "action_item")
    private String actionItem;

    @Column(name = "initiated_by")
    private String initiatedBy;

    @Column(name = "enquiry_type")
    private String enquiryType;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public Long getCallOrderDetailsId() {
		return callOrderDetailsId;
	}

	public void setCallOrderDetailsId(Long callOrderDetailsId) {
		this.callOrderDetailsId = callOrderDetailsId;
	}

	public String getRecordingName() {
		return recordingName;
	}

	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
	}

	public String getExtractedInstrumentName() {
		return extractedInstrumentName;
	}

	public void setExtractedInstrumentName(String extractedInstrumentName) {
		this.extractedInstrumentName = extractedInstrumentName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(BigDecimal stopLoss) {
		this.stopLoss = stopLoss;
	}

	public BigDecimal getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(BigDecimal targetPrice) {
		this.targetPrice = targetPrice;
	}

	public String getTranscription() {
		return transcription;
	}

	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public LocalDateTime getOrderPlacedTime() {
		return orderPlacedTime;
	}

	public void setOrderPlacedTime(LocalDateTime orderPlacedTime) {
		this.orderPlacedTime = orderPlacedTime;
	}

	public String getActionItem() {
		return actionItem;
	}

	public void setActionItem(String actionItem) {
		this.actionItem = actionItem;
	}

	public String getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public String getEnquiryType() {
		return enquiryType;
	}

	public void setEnquiryType(String enquiryType) {
		this.enquiryType = enquiryType;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

    
}
