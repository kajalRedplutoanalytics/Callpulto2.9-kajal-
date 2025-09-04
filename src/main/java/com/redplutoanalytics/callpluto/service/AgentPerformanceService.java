package com.redplutoanalytics.callpluto.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.AgentPerformanceRepositoryCustom;

@Service
public class AgentPerformanceService {
	
	 @Autowired
	    private AgentPerformanceRepositoryCustom repository;

	    public Map<String, Object> getAgentPerformance(String timeFilter, Map<String, String> filters) {
	        return repository.fetchAgentMetrics(timeFilter, filters);
	    }

	    public List<Map<String, Object>> getAgentPositiveNegativeWords(String timeFilter, Map<String, String> filters) {
	        return repository.getAgentPositiveNegativeWords(timeFilter, filters);
	    }

	    public List<String> getRmNames() {
	        return repository.getRmNames();
	    }
	}
	
	


