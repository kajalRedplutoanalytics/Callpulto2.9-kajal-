package com.redplutoanalytics.callpluto.dto;

import java.util.Map;

public class DashboardMetricsDto {

	private Map<String, Object> totalCalls;
	private Map<String, Object> matchCount;
	private Map<String, Object> mismatchCount;
	private Map<String, Object> csatScore;

	public Map<String, Object> getTotalCalls() {
		return totalCalls;
	}

	public void setTotalCalls(Map<String, Object> totalCalls) {
		this.totalCalls = totalCalls;
	}

	public Map<String, Object> getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(Map<String, Object> matchCount) {
		this.matchCount = matchCount;
	}

	public Map<String, Object> getMismatchCount() {
		return mismatchCount;
	}

	public void setMismatchCount(Map<String, Object> mismatchCount) {
		this.mismatchCount = mismatchCount;
	}

	public Map<String, Object> getCsatScore() {
		return csatScore;
	}

	public void setCsatScore(Map<String, Object> csatScore) {
		this.csatScore = csatScore;
	}

}