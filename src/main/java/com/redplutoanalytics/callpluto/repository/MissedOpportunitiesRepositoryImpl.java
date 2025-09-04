package com.redplutoanalytics.callpluto.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MissedOpportunitiesRepositoryImpl implements MissedOpportunitiesRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final String schema;

    public MissedOpportunitiesRepositoryImpl(JdbcTemplate jdbcTemplate,
                                             @Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.schema = schema;
    }

    @Override
    public Map<String, Object> fetchMissedOpportunityStats(String timeFilter, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT mbo.mob_id)
                FROM %s.miss_business_opportunity mbo
                LEFT JOIN %s.recording_details r ON mbo.recording_id = r.recording_id
                LEFT JOIN %s.employee_details e ON r.rm_id = e.rm_id
                WHERE LOWER(mbo.mbo_status) IN ('y', 'yes')
            """.formatted(schema, schema, schema));

        List<Object> params = new ArrayList<>();
        addTimeFilter(sql, timeFilter);
        addRmNameFilter(sql, params, filters);

        if (filters.containsKey("region")) {
            sql.append(" AND e.city = ?");
            params.add(filters.get("region"));
        }

        logSql("Stats", sql, params);
        Integer totalMissed = jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMissed", Map.of("value", totalMissed, "change", 0));
        return stats;
    }

    @Override
    public List<Map<String, Object>> fetchOpportunityTypes(String timeFilter, Map<String, String> rawFilters) {
        Map<String, String> filters = cleanFilters(rawFilters);

        StringBuilder sql = new StringBuilder("""
                SELECT mbo.opportunity_type, COUNT(DISTINCT mbo.mob_id) AS count
                FROM %s.miss_business_opportunity mbo
                LEFT JOIN %s.recording_details r ON mbo.recording_id = r.recording_id
                LEFT JOIN %s.employee_details e ON r.rm_id = e.rm_id
                WHERE LOWER(mbo.mbo_status) IN ('y','yes')
            """.formatted(schema, schema, schema));

        List<Object> params = new ArrayList<>();
        addTimeFilter(sql, timeFilter);
        addRmNameFilter(sql, params, filters);

        sql.append(" GROUP BY mbo.opportunity_type ORDER BY count DESC ");

        logSql("Opportunity Types", sql, params);
        return jdbcTemplate.query(sql.toString(), params.toArray(),
                (rs, rowNum) -> Map.of("type", rs.getString("opportunity_type"), "count", rs.getInt("count")));
    }

    @Override
    public Map<String, Object> fetchMissedByAgent(String timeFilter, Map<String, String> rawFilters) {
        Map<String, String> filters = cleanFilters(rawFilters);

        StringBuilder sql = new StringBuilder("""
                SELECT COALESCE(CONCAT(e.first_name,' ',e.last_name), 'Unknown') AS agent_name,
                       COUNT(DISTINCT mbo.mob_id) AS count
                FROM %s.miss_business_opportunity mbo
                LEFT JOIN %s.recording_details r ON mbo.recording_id = r.recording_id
                LEFT JOIN %s.employee_details e ON r.rm_id = e.rm_id
                WHERE LOWER(mbo.mbo_status) IN ('y','yes')
            """.formatted(schema, schema, schema));

        List<Object> params = new ArrayList<>();
        addTimeFilter(sql, timeFilter);
        addRmNameFilter(sql, params, filters);

        sql.append(" GROUP BY agent_name ORDER BY count DESC LIMIT 5 ");

        logSql("Missed by Agent", sql, params);

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        jdbcTemplate.query(sql.toString(), params.toArray(), rs -> {
            labels.add(rs.getString("agent_name"));
            data.add(rs.getInt("count"));
        });

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Missed Opportunities");
        dataset.put("data", data);

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("datasets", List.of(dataset));

        return response;
    }

    @Override
    public Map<String, Object> fetchMissedOpportunityTrend(String timeFilter, Map<String, String> rawFilters) {
        Map<String, String> filters = cleanFilters(rawFilters);

        StringBuilder sql = new StringBuilder("""
            SELECT TO_CHAR(mbo.created_date, 'YYYY-MM-DD') AS day, COUNT(DISTINCT mbo.mob_id) AS count
            FROM %s.miss_business_opportunity mbo
            LEFT JOIN %s.recording_details r ON mbo.recording_id = r.recording_id
            LEFT JOIN %s.employee_details e ON r.rm_id = e.rm_id
            WHERE LOWER(mbo.mbo_status) IN ('y','yes')
        """.formatted(schema, schema, schema));

        List<Object> params = new ArrayList<>();
        addTimeFilter(sql, timeFilter);
        addRmNameFilter(sql, params, filters);

        sql.append(" GROUP BY TO_CHAR(mbo.created_date, 'YYYY-MM-DD') ORDER BY day");

        logSql("Trend", sql, params);

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        jdbcTemplate.query(sql.toString(), params.toArray(), rs -> {
            labels.add(rs.getString("day"));
            data.add(rs.getInt("count"));
        });

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Missed Opportunities");
        dataset.put("data", data);

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("datasets", List.of(dataset));

        return response;
    }

    private void addTimeFilter(StringBuilder sql, String timeFilter) {
        if (timeFilter == null || timeFilter.isBlank() || "all".equalsIgnoreCase(timeFilter)) {
            return;
        }
        sql.append(" AND mbo.created_date >= NOW() - INTERVAL '").append(getInterval(timeFilter)).append("' ");
    }

    private void addRmNameFilter(StringBuilder sql, List<Object> params, Map<String, String> filters) {
        String rmName = filters.get("rm_name");
        if (rmName == null || rmName.isBlank()) return;
        sql.append(" AND CONCAT(e.first_name,' ',e.last_name) ILIKE ? ");
        params.add('%' + rmName + '%');
    }

    private Map<String, String> cleanFilters(Map<String, String> in) {
        Map<String, String> out = new HashMap<>();
        if (in == null) return out;
        in.forEach((k, v) -> {
            if (v != null) {
                String t = v.trim();
                if (!t.isEmpty()) out.put(k, t);
            }
        });
        return out;
    }

    private String getInterval(String timeFilter) {
        switch (timeFilter.toLowerCase()) {
            case "day": return "1 day";
            case "week": return "7 days";
            case "month": return "1 month";
            case "quarter": return "3 months";
            case "year": return "1 year";
            default: return "7 days";
        }
    }

    private void logSql(String label, StringBuilder sql, List<Object> params) {
        System.out.println("SQL (" + label + "): " + sql);
        System.out.println("Params: " + params);
    }
    
    @Override
    public List<Map<String, Object>> fetchTopAgentsByMissedRevenue(String timeFilter, Map<String, String> filters) {
        String sql = """
            SELECT e.rm_id,
                   COALESCE(e.first_name,'') || ' ' || COALESCE(e.last_name,'') AS agent_name,
                   COUNT(mbo.mob_id) AS total_missed_opportunity,
                   SUM(COALESCE(p.average_revenue_per_transaction,0)) AS missed_revenue_loss,
                   SUM(COALESCE(p.average_commission,0)) AS missed_commission_loss
            FROM callpluto_test.miss_business_opportunity mbo
            JOIN callpluto_test.recording_details rd ON mbo.recording_id = rd.recording_id
            JOIN callpluto_test.employee_details e ON rd.rm_id = e.rm_id
            LEFT JOIN callpluto_test.product_details p 
                   ON TRIM(LOWER(mbo.product_name)) = TRIM(LOWER(p.product_name))
            WHERE LOWER(mbo.mbo_status) IN ('y','yes')
            GROUP BY e.rm_id, e.first_name, e.last_name
            ORDER BY missed_revenue_loss DESC, missed_commission_loss DESC
            LIMIT 5
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("rm_id", rs.getString("rm_id"));
            map.put("agent_name", rs.getString("agent_name"));
            map.put("total_missed_opportunity", rs.getInt("total_missed_opportunity"));
            map.put("missed_revenue_loss", rs.getBigDecimal("missed_revenue_loss"));
            map.put("missed_commission_loss", rs.getBigDecimal("missed_commission_loss"));
            return map;
        });
    }
    @Override
    public List<Map<String, Object>> fetchTopAgentsByMissedCommission(String timeFilter, Map<String, String> filters) {
        String sql = """
            SELECT e.rm_id,
                   COALESCE(e.first_name,'') || ' ' || COALESCE(e.last_name,'') AS agent_name,
                   COUNT(mbo.mob_id) AS total_missed_opportunity,
                   SUM(COALESCE(p.average_revenue_per_transaction,0)) AS missed_revenue_loss,
                   SUM(COALESCE(p.average_commission,0)) AS missed_commission_loss
            FROM callpluto_test.miss_business_opportunity mbo
            JOIN callpluto_test.recording_details rd ON mbo.recording_id = rd.recording_id
            JOIN callpluto_test.employee_details e ON rd.rm_id = e.rm_id
            LEFT JOIN callpluto_test.product_details p 
                   ON TRIM(LOWER(mbo.product_name)) = TRIM(LOWER(p.product_name))
            WHERE LOWER(mbo.mbo_status) IN ('y','yes')
            GROUP BY e.rm_id, e.first_name, e.last_name
            ORDER BY missed_commission_loss DESC, missed_revenue_loss DESC
            LIMIT 5
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("rm_id", rs.getString("rm_id"));
            map.put("agent_name", rs.getString("agent_name"));
            map.put("total_missed_opportunity", rs.getInt("total_missed_opportunity"));
            map.put("missed_revenue_loss", rs.getBigDecimal("missed_revenue_loss"));
            map.put("missed_commission_loss", rs.getBigDecimal("missed_commission_loss"));
            return map;
        });
    }

    @Override
    public List<Map<String, Object>> fetchProductCommissionRevenueRatio(String timeFilter, Map<String, String> rawFilters) {
        Map<String, String> filters = cleanFilters(rawFilters);

        StringBuilder sql = new StringBuilder("""
            SELECT 
                p.product_name,
                COUNT(mbo.mob_id) AS total_missed_opportunity,
                COUNT(mbo.mob_id) * p.average_commission AS missed_commission,
                COUNT(mbo.mob_id) * p.average_revenue_per_transaction AS missed_revenue,
                ROUND(
                    (COUNT(mbo.mob_id) * p.average_revenue_per_transaction)::numeric 
                    / NULLIF((COUNT(mbo.mob_id) * p.average_commission), 0), 
                    2
                ) AS revenue_ratio
            FROM %s.miss_business_opportunity mbo
            JOIN %s.product_details p 
                   ON mbo.product_name = p.product_name
            WHERE mbo.mbo_status = 'Yes'
            """.formatted(schema, schema));

        List<Object> params = new ArrayList<>();

        sql.append(" GROUP BY p.product_name, p.average_commission, p.average_revenue_per_transaction");
        sql.append(" ORDER BY revenue_ratio DESC NULLS LAST");

        logSql("Product commission/revenue ", sql, params);

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("product_name", rs.getString("product_name"));
            map.put("total_missed_opportunity", rs.getInt("total_missed_opportunity"));
            map.put("missed_commission", rs.getBigDecimal("missed_commission"));
            map.put("missed_revenue", rs.getBigDecimal("missed_revenue"));
            map.put("revenue_ratio", rs.getBigDecimal("revenue_ratio"));
            return map;
        });
    }



}