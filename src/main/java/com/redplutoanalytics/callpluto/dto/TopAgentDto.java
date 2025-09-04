package com.redplutoanalytics.callpluto.dto;

import java.util.List;

public class TopAgentDto {

	private List<String> labels;
    private List<ChartDatasetDto> datasets;
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public List<ChartDatasetDto> getDatasets() {
		return datasets;
	}
	public void setDatasets(List<ChartDatasetDto> datasets) {
		this.datasets = datasets;
	}
    
    
    
}