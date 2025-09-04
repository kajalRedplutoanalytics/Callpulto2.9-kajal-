package com.redplutoanalytics.callpluto.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface AgentPerformanceRepositoryCustom {
	
	
	 Map<String, Object> fetchAgentMetrics(String timeFilter, Map<String, String> filters);
	    List<Map<String, Object>> getAgentPositiveNegativeWords(String timeFilter, Map<String, String> filters);
	    List<String> getRmNames();
	}
