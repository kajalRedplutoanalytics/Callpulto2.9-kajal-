package com.redplutoanalytics.callpluto.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class DashboardRepository {

	private final JdbcTemplate jdbcTemplate;
	private final String schema;

	public DashboardRepository(JdbcTemplate jdbcTemplate,
			@Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
		this.jdbcTemplate = jdbcTemplate;
		this.schema = schema;
	}

	public static class Metrics {
		public int totalCalls;
		public int matchCount;
		public int mismatchCount;

		public Metrics(int totalCalls, int matchCount, int mismatchCount) {
			this.totalCalls = totalCalls;
			this.matchCount = matchCount;
			this.mismatchCount = mismatchCount;
		}
	}

	// Build filters for dimension attributes
	private List<Object> buildFilters(Map<String, String> filters, List<String> where, String aliasCall,
			String deptAlias, String empAlias, String prodAlias, String locAlias) {
		List<Object> params = new ArrayList<>();
		if (filters != null) {
			if (filters.get("department") != null) {
				where.add(deptAlias + ".dept_name = ?");
				params.add(filters.get("department"));
			}
			if (filters.get("rm_name") != null) {
				where.add("(" + empAlias + ".first_name || ' ' || " + empAlias + ".last_name) ILIKE ?");
				params.add("%" + filters.get("rm_name") + "%");
			}
			if (filters.get("product") != null) {
				where.add(prodAlias + ".product_name = ?");
				params.add(filters.get("product"));
			}
			if (filters.get("region") != null) {
				where.add(locAlias + ".state = ?");
				params.add(filters.get("region"));
			}
			if (filters.get("call_type") != null) {
				where.add(aliasCall + ".call_type = ?");
				params.add(filters.get("call_type").toLowerCase());
			}
		}
		return params;
	}

	// ---------------------- KPI Metrics ----------------------
	public Metrics getKpiMetrics(LocalDateTime start, LocalDateTime end, Map<String, String> filters) {
		// Total Calls
		List<String> whereCalls = new ArrayList<>();
		whereCalls.add("cod.created_date BETWEEN ? AND ?");
		List<Object> callsParams = new ArrayList<>(Arrays.asList(start, end));
		callsParams.addAll(buildFilters(filters, whereCalls, "rd", "d", "e", "p", "l"));
		String callsWhere = String.join(" AND ", whereCalls);

		String totalCallsSql = String.format("""
				    SELECT COUNT(DISTINCT cod.recording_name) AS total_calls
				    FROM %s.call_order_details cod
				    LEFT JOIN %s.recording_details rd ON cod.recording_name = rd.recording_name
				    LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
				    LEFT JOIN %s.department_details d ON e.department_id = d.department_id
				    LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
				    LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
				    LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
				    LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
				    LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
				    WHERE %s
				""", schema, schema, schema, schema, schema, schema, schema, schema, schema, callsWhere);

		// QA Counts (Match / Mismatch)
		List<String> whereValidation = new ArrayList<>();
		whereValidation.add("qa.created_date BETWEEN ? AND ?");
		List<Object> validationParams = new ArrayList<>(Arrays.asList(start, end));
		validationParams.addAll(buildFilters(filters, whereValidation, "qa", "d", "e", "p", "l"));
		String validationWhere = String.join(" AND ", whereValidation);

		String qaSql = String.format(
				"""
						    SELECT
						      COALESCE(SUM(CASE WHEN UPPER(final_qa_status) = 'MATCH' THEN 1 ELSE 0 END), 0) AS match_count,
						      COALESCE(SUM(CASE WHEN UPPER(final_qa_status) = 'MISMATCH' THEN 1 ELSE 0 END), 0) AS mismatch_count
						    FROM %s.qa_validation_output qa
						    LEFT JOIN %s.recording_details rd ON qa.trading_order_details_id = rd.trading_order_details_id
						    LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
						    LEFT JOIN %s.department_details d ON e.department_id = d.department_id
						    LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
						    LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
						    LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
						    LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
						    LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
						    WHERE %s
						""",
				schema, schema, schema, schema, schema, schema, schema, schema, schema, validationWhere);

		int totalCalls = 0;
		int matchCount = 0;
		int mismatchCount = 0;

		try {
			Integer tc = jdbcTemplate.queryForObject(totalCallsSql, callsParams.toArray(), Integer.class);
			totalCalls = tc != null ? tc : 0;

			Map<String, Object> result = jdbcTemplate.queryForMap(qaSql, validationParams.toArray());
			Number matchNum = (Number) result.get("match_count");
			Number mismatchNum = (Number) result.get("mismatch_count");
			matchCount = matchNum != null ? matchNum.intValue() : 0;
			mismatchCount = mismatchNum != null ? mismatchNum.intValue() : 0;

		} catch (Exception ex) {
			System.err.println("Error executing KPI metrics query: " + ex.getMessage());
		}

		return new Metrics(totalCalls, matchCount, mismatchCount);
	}

	// ---------------------- CSAT Score ----------------------
	// ---------------------- CSAT Score ----------------------
	public Map<String, Object> getCSATScore(LocalDateTime start, LocalDateTime end, LocalDateTime prevStart,
			LocalDateTime prevEnd, Map<String, String> filters) {
		// Build filters for CSAT table and dimension attributes
		List<String> where = new ArrayList<>();
		where.add("cas.created_date BETWEEN ? AND ?");
		List<Object> params = new ArrayList<>(Arrays.asList(start, end));
		params.addAll(buildFilters(filters, where, "rd", "d", "e", "p", "l"));
		String whereClause = String.join(" AND ", where);

		String csatSql = String.format("""
				    SELECT AVG(cas.csat_score::numeric) as avg_score
				    FROM %s.csat_and_agent_score cas
				    LEFT JOIN %s.recording_details rd ON cas.recording_id = rd.recording_id
				    LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
				    LEFT JOIN %s.department_details d ON e.department_id = d.department_id
				    LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
				    LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
				    LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
				    LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
				    LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
				    WHERE %s
				""", schema, schema, schema, schema, schema, schema, schema, schema, schema, whereClause);

		Double avgScore = jdbcTemplate.queryForObject(csatSql, params.toArray(), Double.class);
		if (avgScore == null)
			avgScore = 0.0; // Use 0 if no data exists

		// Previous period
		List<Object> prevParams = new ArrayList<>(params);
		prevParams.set(0, prevStart);
		prevParams.set(1, prevEnd);
		Double prevScore = jdbcTemplate.queryForObject(csatSql, prevParams.toArray(), Double.class);
		if (prevScore == null)
			prevScore = 0.0;

		double change = prevScore != 0 ? ((avgScore - prevScore) / prevScore) * 100.0 : 0;

		Map<String, Object> csat = new HashMap<>();
		csat.put("value", String.format("%.1f", avgScore));
		csat.put("change", Math.round(change * 10.0) / 10.0);
		return csat;
	}

	// ---------------------- Match Ratio ----------------------
	public Map<String, Double> getMatchRatio(LocalDateTime start, LocalDateTime end, Map<String, String> filters) {
		List<String> where = new ArrayList<>();
		where.add("qa.created_date BETWEEN ? AND ?");
		List<Object> params = new ArrayList<>(Arrays.asList(start, end));
		params.addAll(buildFilters(filters, where, "qa", "d", "e", "p", "l")); // Apply filters to QA joins
		String whereClause = String.join(" AND ", where);

		String sql = String.format(
				"""
						    SELECT
						      COALESCE(SUM(CASE WHEN UPPER(qa.final_qa_status) = 'MATCH' THEN 1 ELSE 0 END), 0) AS match_count,
						      COALESCE(SUM(CASE WHEN UPPER(qa.final_qa_status) = 'MISMATCH' THEN 1 ELSE 0 END), 0) AS mismatch_count
						    FROM %s.qa_validation_output qa
						    LEFT JOIN %s.recording_details rd ON qa.trading_order_details_id = rd.trading_order_details_id
						    LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
						    LEFT JOIN %s.department_details d ON e.department_id = d.department_id
						    LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
						    LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
						    LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
						    LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
						    LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
						    WHERE %s
						""",
				schema, schema, schema, schema, schema, schema, schema, schema, schema, whereClause);

		Map<String, Object> result = jdbcTemplate.queryForMap(sql, params.toArray());
		int matchCount = ((Number) result.get("match_count")).intValue();
		int mismatchCount = ((Number) result.get("mismatch_count")).intValue();

		int total = matchCount + mismatchCount;
		double matchPercentage = (total > 0) ? (matchCount * 100.0 / total) : 0;
		double mismatchPercentage = (total > 0) ? (mismatchCount * 100.0 / total) : 0;

		Map<String, Double> res = new HashMap<>();
		res.put("matchPercentage", matchPercentage);
		res.put("mismatchPercentage", mismatchPercentage);
		return res;
	}

	// ---------------------- Top Agents ----------------------
	public List<Map<String, Object>> getTopAgents(LocalDateTime start, LocalDateTime end, Map<String, String> filters) {
		List<String> where = new ArrayList<>();
		where.add("cas.created_date BETWEEN ? AND ?");
		List<Object> params = new ArrayList<>(Arrays.asList(start, end));
		params.addAll(buildFilters(filters, where, "rd", "d", "e", "p", "l"));
		String whereClause = String.join(" AND ", where);

		String sql = String.format("""
				    WITH distinct_recordings AS (
				      SELECT DISTINCT ON (recording_id) *
				      FROM %s.recording_details
				      ORDER BY recording_id, created_date DESC
				    ), agent_stats AS (
				      SELECT e.rm_id AS employee_id,
				             e.first_name || ' ' || e.last_name AS employee_name,
				             COALESCE(cas.agent_performance_score, 0) AS score
				      FROM %s.employee_details e
				      LEFT JOIN %s.csat_and_agent_score cas ON e.rm_id = cas.rm_id
				      LEFT JOIN %s.department_details d ON e.department_id = d.department_id
				      LEFT JOIN %s.client_details cl ON cas.client_id = cl.client_id
				      LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
				      LEFT JOIN distinct_recordings r ON cas.recording_id = r.recording_id
				      LEFT JOIN %s.trading_order_details t ON r.trading_order_details_id = t.trading_order_details_id
				      LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
				      LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
				      WHERE %s
				    )
				    SELECT employee_name, ROUND(AVG(score)::numeric, 2) AS avg_score
				    FROM agent_stats
				    GROUP BY employee_name
				    ORDER BY avg_score DESC
				    LIMIT 5
				""", schema, schema, schema, schema, schema, schema, schema, schema, schema, whereClause);

		return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("agentName", rs.getString("employee_name"));
			map.put("avgScore", rs.getDouble("avg_score"));
			return map;
		});
	}

	// ---------------------- Call Volume ----------------------
	public List<Map<String, Object>> getCallVolume(LocalDateTime start, LocalDateTime end, String interval,
			Map<String, String> filters) {
		List<String> where = new ArrayList<>();
		where.add("cod.created_date BETWEEN ? AND ?");
		List<Object> params = new ArrayList<>(Arrays.asList(start, end));
		params.addAll(buildFilters(filters, where, "rd", "d", "e", "p", "l"));
		String whereClause = String.join(" AND ", where);

		String sql = String.format("""
				WITH time_series AS (
				SELECT generate_series(
				date_trunc('%s', ?::timestamp),
				date_trunc('%s', ?::timestamp),
				'1 %s'::interval
				) as time_bucket
				), call_counts AS (
				SELECT date_trunc('%s', cod.created_date) as time_bucket,
				COUNT(DISTINCT cod.recording_name) as call_count
				FROM %s.call_order_details cod
				LEFT JOIN %s.recording_details rd ON cod.recording_name = rd.recording_name
				LEFT JOIN %s.employee_details e ON rd.rm_id = e.rm_id
				LEFT JOIN %s.department_details d ON e.department_id = d.department_id
				LEFT JOIN %s.client_details cl ON rd.client_id = cl.client_id
				LEFT JOIN %s.location_details l ON cl.location_id = l.location_id
				LEFT JOIN %s.trading_order_details t ON rd.trading_order_details_id = t.trading_order_details_id
				LEFT JOIN %s.equity_details eq ON t.instrument_id = eq.instrument_id
				LEFT JOIN %s.product_details p ON eq.product_details_id = p.product_details_id
				WHERE %s
				GROUP BY date_trunc('%s', cod.created_date)
				)
				SELECT ts.time_bucket, COALESCE(cc.call_count, 0) as call_count
				FROM time_series ts
				LEFT JOIN call_counts cc ON ts.time_bucket = cc.time_bucket
				ORDER BY ts.time_bucket
				""", interval, interval, interval, interval, schema, schema, schema, schema, schema, schema, schema,
				schema, schema, whereClause, interval);

// Parameters for generate_series + filtering
		List<Object> allParams = new ArrayList<>();
		allParams.add(start); // generate_series start
		allParams.add(end); // generate_series end
		allParams.addAll(params); // filtering params

		return jdbcTemplate.query(sql, allParams.toArray(), (rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("time", rs.getTimestamp("time_bucket").toLocalDateTime().toString());
			map.put("count", rs.getInt("call_count"));
			return map;
		});
	}
}