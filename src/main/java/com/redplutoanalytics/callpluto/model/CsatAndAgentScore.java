
package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "csat_and_agent_score")
public class CsatAndAgentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ca_id")
    private Long caId;

    @Column(name = "recording_id")
    private String recordingId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "rm_id")
    private String rmId;

    @Column(name = "csat_score")
    private Integer csatScore;

    @Column(name = "agent_performance_score")
    private Integer agentPerformanceScore;

    @Column(name = "positive_words")
    private String positiveWords;

    @Column(name = "negative_words")
    private String negativeWords;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

	public Long getCaId() {
		return caId;
	}

	public void setCaId(Long caId) {
		this.caId = caId;
	}

	public String getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(String recordingId) {
		this.recordingId = recordingId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRmId() {
		return rmId;
	}

	public void setRmId(String rmId) {
		this.rmId = rmId;
	}

	public Integer getCsatScore() {
		return csatScore;
	}

	public void setCsatScore(Integer csatScore) {
		this.csatScore = csatScore;
	}

	public Integer getAgentPerformanceScore() {
		return agentPerformanceScore;
	}

	public void setAgentPerformanceScore(Integer agentPerformanceScore) {
		this.agentPerformanceScore = agentPerformanceScore;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

   
}
