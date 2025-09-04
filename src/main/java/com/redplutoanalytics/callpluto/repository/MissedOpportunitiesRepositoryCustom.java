package com.redplutoanalytics.callpluto.repository;
 
import java.util.List;
import java.util.Map;
 
import org.springframework.stereotype.Repository;
 
@Repository
public interface MissedOpportunitiesRepositoryCustom {
	 Map<String, Object> fetchMissedOpportunityStats(String timeFilter, Map<String, String> filters);

	    List<Map<String, Object>> fetchOpportunityTypes(String timeFilter, Map<String, String> filters);

	    Map<String, Object> fetchMissedByAgent(String timeFilter, Map<String, String> filters);

	    Map<String, Object> fetchMissedOpportunityTrend(String timeFilter, Map<String, String> filters);
	    
	    
	    List<Map<String, Object>> fetchTopAgentsByMissedRevenue(String timeFilter, Map<String, String> filters);

	    List<Map<String, Object>> fetchTopAgentsByMissedCommission(String timeFilter, Map<String, String> filters);
	    
	    
	    
	    List<Map<String, Object>> fetchProductCommissionRevenueRatio(String timeFilter, Map<String, String> filters);
	}