package com.redplutoanalytics.callpluto.service;
 
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.MissedOpportunitiesRepositoryCustom;
 
@Service
public class MissedOpportunitiesService {
 
    @Autowired
    private MissedOpportunitiesRepositoryCustom repository;
 
    public Map<String, Object> getMissedOpportunityStats(String timeFilter, Map<String, String> filters) {
        return repository.fetchMissedOpportunityStats(timeFilter, filters);
    }
 
    public List<Map<String, Object>> getOpportunityTypes(String timeFilter, Map<String, String> filters) {
        return repository.fetchOpportunityTypes(timeFilter, filters);
    }
 
    public Map<String, Object> getMissedByAgent(String timeFilter, Map<String, String> filters) {
        return repository.fetchMissedByAgent(timeFilter, filters);
    }
    public Map<String, Object> getMissedOpportunityTrend(String timeFilter, Map<String, String> filters) {
        return repository.fetchMissedOpportunityTrend(timeFilter, filters);
    }
    
    
    public List<Map<String, Object>> getTopAgentsByMissedRevenue(String timeFilter, Map<String, String> filters) {
        return repository.fetchTopAgentsByMissedRevenue(timeFilter, filters);
    }

    public List<Map<String, Object>> getTopAgentsByMissedCommission(String timeFilter, Map<String, String> filters) {
        return repository.fetchTopAgentsByMissedCommission(timeFilter, filters);
    }
    public List<Map<String, Object>> getProductCommissionRevenueRatio(String timeFilter, Map<String, String> filters) {
    	return repository.fetchProductCommissionRevenueRatio(timeFilter, filters);
    	}
    	
}
 