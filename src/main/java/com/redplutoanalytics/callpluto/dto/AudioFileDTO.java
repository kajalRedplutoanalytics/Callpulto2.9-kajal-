package com.redplutoanalytics.callpluto.dto;

import java.time.LocalDateTime;

public class AudioFileDTO {

	private Long id;
    private String recordingName;   // ✅ Newly added field
    private String fileName;
    private String duration;
    private String transcript;
    private String translation;
    private LocalDateTime uploadDate;
    private LocalDateTime createdDate;
    private String rmName;
    private Integer csatScore;
    private String positiveWords;
    private String negativeWords;
    private String callType;
    private String summary;
    private String actionItem;
    private String status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRecordingName() {
		return recordingName;
	}
	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getTranscript() {
		return transcript;
	}
	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}
	public String getTranslation() {
		return translation;
	}
	public void setTranslation(String translation) {
		this.translation = translation;
	}
	public LocalDateTime getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public String getRmName() {
		return rmName;
	}
	public void setRmName(String rmName) {
		this.rmName = rmName;
	}
	public Integer getCsatScore() {
		return csatScore;
	}
	public void setCsatScore(Integer csatScore) {
		this.csatScore = csatScore;
	}
	public String getPositiveWords() {
		return positiveWords;
	}
	public void setPositiveWords(String positiveWords) {
		this.positiveWords = positiveWords;
	}
	public String getNegativeWords() {
		return negativeWords;
	}
	public void setNegativeWords(String negativeWords) {
		this.negativeWords = negativeWords;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getActionItem() {
		return actionItem;
	}
	public void setActionItem(String actionItem) {
		this.actionItem = actionItem;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

    
}