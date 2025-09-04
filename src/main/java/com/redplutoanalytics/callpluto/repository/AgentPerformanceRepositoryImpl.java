package com.redplutoanalytics.callpluto.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AgentPerformanceRepositoryImpl implements AgentPerformanceRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final String schema;

    public AgentPerformanceRepositoryImpl(JdbcTemplate jdbcTemplate,
                                          @Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.schema = schema;
    }

    @Override
    public Map<String, Object> fetchAgentMetrics(String timeFilter, Map<String, String> filters) {
        try {
            StringBuilder sql = new StringBuilder(
                String.format("""
                    SELECT 
                        COUNT(DISTINCT r.recording_name) AS total_calls,
                        ROUND(AVG(c.agent_performance_score)::numeric, 2) AS avg_satisfaction,
                        ROUND(AVG(c.csat_score)::numeric, 2) AS avg_csat
                    FROM %s.csat_and_agent_score c
                    JOIN %s.recording_details r ON c.client_id = r.client_id AND c.rm_id = r.rm_id
                    JOIN %s.employee_details e ON c.rm_id = e.rm_id
                    LEFT JOIN %s.department_details d ON e.department_id = d.department_id
                    LEFT JOIN %s.client_details cl ON r.client_id = cl.client_id
                    LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
                    LEFT JOIN %s.trading_order_details t ON r.trading_order_details_id = t.trading_order_details_id
                    WHERE c.created_date >= NOW() - INTERVAL '%s'
                    """, schema, schema, schema, schema, schema, schema, schema, getInterval(timeFilter))
            );

            List<Object> params = new ArrayList<>();
            appendFilters(sql, params, filters);

            return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), agentMetricsMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("totalCallsReviewed", 0, "avgSatisfactionScore", 0.0, "avgCSATScore", 0.0);
        }
    }

    @Override
    public List<Map<String, Object>> getAgentPositiveNegativeWords(String timeFilter, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder(
            String.format("""
                SELECT
                    e.first_name || ' ' || e.last_name AS rm_name,
                    STRING_AGG(DISTINCT c.positive_words, ',') AS positive_words,
                    STRING_AGG(DISTINCT c.negative_words, ',') AS negative_words
                FROM %s.csat_and_agent_score c
                JOIN %s.recording_details r ON c.client_id = r.client_id AND c.rm_id = r.rm_id
                JOIN %s.employee_details e ON c.rm_id = e.rm_id
                LEFT JOIN %s.department_details d ON e.department_id = d.department_id
                LEFT JOIN %s.client_details cl ON r.client_id = cl.client_id
                LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
                LEFT JOIN %s.trading_order_details t ON r.trading_order_details_id = t.trading_order_details_id
                WHERE c.created_date >= NOW() - INTERVAL '%s'
                """, schema, schema, schema, schema, schema, schema, schema, getInterval(timeFilter))
        );

        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, filters);

        sql.append(" GROUP BY rm_name ORDER BY rm_name");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("rmName", rs.getString("rm_name"));
            map.put("positiveWords", splitWords(rs.getString("positive_words")));
            map.put("negativeWords", splitWords(rs.getString("negative_words")));
            return map;
        });
    }

    @Override
    public List<String> getRmNames() {
        return jdbcTemplate.query(
                String.format("SELECT DISTINCT first_name || ' ' || last_name AS full_name FROM %s.employee_details ORDER BY full_name", schema),
                (rs, rowNum) -> rs.getString("full_name"));
    }

    // ----------------- Helpers --------------------

    private String getInterval(String timeFilter) {
        return switch (timeFilter == null ? "" : timeFilter.toLowerCase()) {
            case "month" -> "1 month";
            case "day" -> "1 day";
            default -> "7 days";
        };
    }

    private void appendFilters(StringBuilder sql, List<Object> params, Map<String, String> filters) {
        if (filters.containsKey("department")) {
            sql.append(" AND d.dept_name = ?");
            params.add(filters.get("department"));
        }
        if (filters.containsKey("rm_name")) {
            sql.append(" AND CONCAT(e.first_name, ' ', e.last_name) = ?");
            params.add(filters.get("rm_name"));
        }
        if (filters.containsKey("region")) {
            sql.append(" AND l.region = ?");
            params.add(filters.get("region"));
        }
        if (filters.containsKey("product")) {
            sql.append(" AND t.product_name = ?");
            params.add(filters.get("product"));
        }
    }

    private RowMapper<Map<String, Object>> agentMetricsMapper() {
        return (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("totalCallsReviewed", rs.getInt("total_calls"));
            map.put("avgSatisfactionScore", rs.getDouble("avg_satisfaction"));
            map.put("avgCSATScore", rs.getDouble("avg_csat"));
            return map;
        };
    }

    private List<String> splitWords(String input) {
        if (input == null || input.isBlank())
            return List.of();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
