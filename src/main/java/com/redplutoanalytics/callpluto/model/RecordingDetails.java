package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recording_details")
public class RecordingDetails {

    @Id
    @Column(name = "recording_details_id")
    private String recordingDetailsId;

    @Column(name = "recording_id")
    private String recordingId;

    @Column(name = "recording_name")
    private String recordingName;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientDetails client;

    @ManyToOne
    @JoinColumn(name = "rm_id")
    private EmployeeDetails rm;

    @ManyToOne
    @JoinColumn(name = "trading_order_details_id")
    private TradingOrderDetails tradingOrder;

    @Column(name = "recording_flag")
    private String recordingFlag;

    @Column(name = "call_type")
    private String callType;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public String getRecordingDetailsId() {
		return recordingDetailsId;
	}

	public void setRecordingDetailsId(String recordingDetailsId) {
		this.recordingDetailsId = recordingDetailsId;
	}

	public String getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(String recordingId) {
		this.recordingId = recordingId;
	}

	public String getRecordingName() {
		return recordingName;
	}

	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
	}

	public ClientDetails getClient() {
		return client;
	}

	public void setClient(ClientDetails client) {
		this.client = client;
	}

	public EmployeeDetails getRm() {
		return rm;
	}

	public void setRm(EmployeeDetails rm) {
		this.rm = rm;
	}

	public TradingOrderDetails getTradingOrder() {
		return tradingOrder;
	}

	public void setTradingOrder(TradingOrderDetails tradingOrder) {
		this.tradingOrder = tradingOrder;
	}

	public String getRecordingFlag() {
		return recordingFlag;
	}

	public void setRecordingFlag(String recordingFlag) {
		this.recordingFlag = recordingFlag;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

    
}
