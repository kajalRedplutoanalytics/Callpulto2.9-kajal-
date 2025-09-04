package com.redplutoanalytics.callpluto.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentPerformanceRepository extends AgentPerformanceRepositoryCustom {

    @Query(value = "SELECT total_calls, avg_satisfaction, avg_csat FROM agent_performance_summary", nativeQuery = true)
    Map<String, Object> fetchAgentMetrics();

}
