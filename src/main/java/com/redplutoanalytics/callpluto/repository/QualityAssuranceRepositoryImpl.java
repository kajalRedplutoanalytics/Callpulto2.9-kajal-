package com.redplutoanalytics.callpluto.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class QualityAssuranceRepositoryImpl implements QualityAssuranceRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String schema;

    public QualityAssuranceRepositoryImpl(JdbcTemplate jdbcTemplate,
                                          @Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.schema = schema;
    }

    // Common joins reused by all queries
    private String commonJoins() {
        return """
            LEFT JOIN %s.recording_details rd ON qv.trading_order_details_id = rd.trading_order_details_id
            LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
            LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
            LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
            LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
            LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
            LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
        """.formatted(schema, schema, schema, schema, schema, schema, schema);
    }

    @Override
    public long countFiltered(String status, String interval, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM %s.qa_validation_output qv
        """.formatted(schema))
                .append(commonJoins())
                .append("""
            WHERE LOWER(qv.final_qa_status) = LOWER(?)
              AND qv.created_date >= NOW() - (?::interval)
        """);

        List<Object> params = new ArrayList<>(List.of(status.toLowerCase(), interval));
        appendFilters(sql, params, filters);

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Long.class);
    }

    @Override
    public Map<String, Object> getMatchRateWithCounts(String interval, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                SUM(CASE WHEN LOWER(qv.final_qa_status) = 'match' THEN 1 ELSE 0 END) AS match_count,
                SUM(CASE WHEN LOWER(qv.final_qa_status) = 'mismatch' THEN 1 ELSE 0 END) AS mismatch_count
            FROM %s.qa_validation_output qv
        """.formatted(schema))
                .append(commonJoins())
                .append("""
            WHERE qv.created_date >= NOW() - (?::interval)
        """);

        List<Object> params = new ArrayList<>(List.of(interval));
        appendFilters(sql, params, filters);

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), (rs, rowNum) -> {
            int match = rs.getInt("match_count");
            int mismatch = rs.getInt("mismatch_count");
            int total = match + mismatch;
            double percent = total > 0 ? (match * 100.0 / total) : 0;

            return Map.of(
                    "change", 0,
                    "matchCount", match,
                    "mismatchCount", mismatch,
                    "total", total,
                    "value", String.format("%.0f%%", percent)
            );
        });
    }

    @Override
    public List<Object[]> countMismatchByInstrument(String interval, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT qv.instrument_name, COUNT(*) FROM %s.qa_validation_output qv
        """.formatted(schema))
                .append(commonJoins())
                .append("""
            WHERE LOWER(qv.final_qa_status) = 'mismatch'
              AND qv.created_date >= NOW() - (?::interval)
        """);

        List<Object> params = new ArrayList<>(List.of(interval));
        appendFilters(sql, params, filters);
        sql.append(" GROUP BY qv.instrument_name");

        return jdbcTemplate.query(sql.toString(), params.toArray(),
                (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    @Override
    public List<Object[]> getTrend(String interval, Map<String, String> filters) {
        // Use subquery to avoid row multiplication from joins
        StringBuilder sql = new StringBuilder("""
            SELECT DATE(qv.created_date) AS date,
                   SUM(CASE WHEN LOWER(qv.final_qa_status) = 'match' THEN 1 ELSE 0 END) AS match_count,
                   SUM(CASE WHEN LOWER(qv.final_qa_status) = 'mismatch' THEN 1 ELSE 0 END) AS mismatch_count
            FROM (
                SELECT * FROM %s.qa_validation_output
                WHERE created_date >= NOW() - (?::interval)
            ) qv
        """.formatted(schema))
                .append(commonJoins())
                .append(" WHERE 1=1 ");

        List<Object> params = new ArrayList<>(List.of(interval));
        appendFilters(sql, params, filters);
        sql.append(" GROUP BY DATE(qv.created_date) ORDER BY DATE(qv.created_date)");

        return jdbcTemplate.query(sql.toString(), params.toArray(),
                (rs, rowNum) -> new Object[]{rs.getDate("date"), rs.getInt("match_count"), rs.getInt("mismatch_count")});
    }

    @Override
    public List<Map<String, Object>> fetchRecords(String status, String interval, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT qv.* FROM %s.qa_validation_output qv
        """.formatted(schema))
                .append(commonJoins())
                .append("""
            WHERE LOWER(qv.final_qa_status) = LOWER(?)
              AND qv.created_date >= NOW() - (?::interval)
        """);

        List<Object> params = new ArrayList<>(List.of(status.toLowerCase(), interval));
        appendFilters(sql, params, filters);
        sql.append(" ORDER BY qv.created_date DESC LIMIT 1000");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private void appendFilters(StringBuilder sql, List<Object> params, Map<String, String> filters) {
        if (filters.containsKey("rm_name")) {
            sql.append(" AND CONCAT(e.first_name, ' ', e.last_name) = ? ");
            params.add(filters.get("rm_name"));
        }
        if (filters.containsKey("region")) {
            sql.append(" AND l.state = ? ");
            params.add(filters.get("region"));
        }
        if (filters.containsKey("product")) {
            sql.append(" AND p.product_name = ? ");
            params.add(filters.get("product"));
        }
        if (filters.containsKey("call_type")) {
            sql.append(" AND qv.call_type = ? ");
            params.add(filters.get("call_type"));
        }
    }
}
