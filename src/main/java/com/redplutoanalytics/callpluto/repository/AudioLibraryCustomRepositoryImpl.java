package com.redplutoanalytics.callpluto.repository;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
 
@Repository
public class AudioLibraryCustomRepositoryImpl implements AudioLibraryRepository {
 
    private final JdbcTemplate jdbcTemplate;
    private final String schema;
 
    public AudioLibraryCustomRepositoryImpl(JdbcTemplate jdbcTemplate,
                                            @Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.schema = schema;
    }
 
   
    private boolean getBoolean(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(String.valueOf(val));
    }
 
    @Override
    public List<Map<String, Object>> findFilteredAudioFiles(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ")
             .append("co.call_order_details_id AS id, ")
             .append("co.recording_name AS recording_name, ")
             .append("co.recording_name || '.ogg' AS file_name, ")
             .append("EXTRACT(EPOCH FROM (rd.end_time - rd.start_time)) AS duration_secs, ")
             .append("co.transcription AS transcript, ")
             .append("co.translation AS translation, ")
             .append("TO_CHAR(co.order_placed_time, 'YYYY-MM-DD\"T\"HH24:MI:SS.MS') AS upload_date, ")
             .append("TO_CHAR(co.order_placed_time, 'YYYY-MM-DD\"T\"HH24:MI:SS.MS') AS created_date, ")
             .append("COALESCE(ed.first_name, '') || ' ' || COALESCE(ed.last_name, '') AS rm_name, ")
             .append("cs.csat_score, cs.positive_words, cs.negative_words, ")
             .append("rd.call_type, co.summary, co.action_item, co.order_status AS status, ")
             .append("qa.final_qa_status AS qa_status ")
             .append("FROM ").append(schema).append(".call_order_details co ")
             .append("LEFT JOIN ").append(schema).append(".recording_details rd ON co.recording_name = rd.recording_name ")
             .append("LEFT JOIN ").append(schema).append(".csat_and_agent_score cs ON rd.recording_id = cs.recording_id ")
             .append("LEFT JOIN ").append(schema).append(".employee_details ed ON cs.rm_id = ed.rm_id ")
             .append("LEFT JOIN ").append(schema).append(".qa_validation_output qa ON co.call_order_details_id = qa.call_order_details_id ")
             .append("WHERE 1=1 ");
 
        List<Object> params = new ArrayList<>();
 
        if (getBoolean(filters.get("mismatch"))) {
            query.append(" AND TRIM(UPPER(qa.final_qa_status)) = 'MISMATCH' ");
        }
 
        
        if (filters.containsKey("rmName")) {
            query.append(" AND (COALESCE(ed.first_name, '') || ' ' || COALESCE(ed.last_name, '')) ILIKE ? ");
            params.add("%" + filters.get("rmName") + "%");
        }
        if (filters.containsKey("region")) {
            query.append(" AND ed.city ILIKE ? ");
            params.add("%" + filters.get("region") + "%");
        }
        if (filters.containsKey("department")) {
            query.append(" AND ed.designation ILIKE ? ");
            params.add("%" + filters.get("department") + "%");
        }
        if (filters.containsKey("product")) {
            query.append(" AND rd.trading_order_details_id IN (")
                 .append("SELECT trading_order_details_id FROM ").append(schema).append(".trading_order_details ")
                 .append("WHERE product_details_id ILIKE ?) ");
            params.add("%" + filters.get("product") + "%");
        }
        if (filters.containsKey("callType")) {
            query.append(" AND rd.call_type ILIKE ? ");
            params.add("%" + filters.get("callType") + "%");
        }
 
      
        return jdbcTemplate.query(query.toString(), params.toArray(), new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getObject("id"));
                row.put("recordingName", rs.getString("recording_name"));
                row.put("fileName", rs.getString("file_name"));
 
                // Format duration mm:ss
                double durationSecs = rs.getDouble("duration_secs");
                String durationFormatted = String.format("%d:%02d", (int) durationSecs / 60, (int) durationSecs % 60);
                row.put("duration", durationFormatted);
 
                row.put("transcript", Optional.ofNullable(rs.getString("transcript")).orElse(""));
                row.put("translation", Optional.ofNullable(rs.getString("translation")).orElse(""));
                row.put("uploadDate", Optional.ofNullable(rs.getString("upload_date")).orElse(""));
                row.put("created_date", Optional.ofNullable(rs.getString("created_date")).orElse(""));
                row.put("rmName", Optional.ofNullable(rs.getString("rm_name")).orElse(""));
                row.put("csat_score", rs.getObject("csat_score"));
                row.put("positive_words", Optional.ofNullable(rs.getString("positive_words")).orElse(""));
                row.put("negative_words", Optional.ofNullable(rs.getString("negative_words")).orElse(""));
                row.put("call_type", Optional.ofNullable(rs.getString("call_type")).orElse(""));
                row.put("summary", Optional.ofNullable(rs.getString("summary")).orElse(""));
                row.put("action_item", Optional.ofNullable(rs.getString("action_item")).orElse(""));
                row.put("status", Optional.ofNullable(rs.getString("status")).orElse(""));
                row.put("qa_status", Optional.ofNullable(rs.getString("qa_status")).orElse(""));
 
                return row;
            }
        });
    }
    
    
    private final DateTimeFormatter[] parsers = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };
 
        private String asString(Object o) {
            return o == null ? "" : o.toString().trim();
        }
 
        private Timestamp toTimestamp(String s) {
            if (s == null || s.trim().isEmpty()) return null;
            s = s.trim();
            for (DateTimeFormatter fmt : parsers) {
                try {
                    LocalDateTime ldt = LocalDateTime.parse(s, fmt);
                    return Timestamp.valueOf(ldt);
                } catch (Exception e) {
                    // try next
                }
            }
            try {
                return Timestamp.valueOf(s);
            } catch (Exception e) {
                return null;
            }
        }
 
        private Double parseDoubleOrNull(String s) {
            if (s == null || s.isEmpty()) return null;
            try { return Double.valueOf(s); } catch (Exception e) { return null; }
        }
 
        @Override
        public void insertTradingOrder(Map<String, Object> row) {
            String sql = String.format("""
                INSERT INTO %s.trading_order_details
                (trading_order_details_id, client_id, rm_id, instrument_id, product_details_id,
                quantity, price, transaction_type, stop_loss, target_price, order_status,
                created_date, order_execution_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (trading_order_details_id) DO UPDATE SET
                    client_id = EXCLUDED.client_id,
                    rm_id = EXCLUDED.rm_id,
                    instrument_id = EXCLUDED.instrument_id,
                    product_details_id = EXCLUDED.product_details_id,
                    quantity = EXCLUDED.quantity,
                    price = EXCLUDED.price,
                    transaction_type = EXCLUDED.transaction_type,
                    stop_loss = EXCLUDED.stop_loss,
                    target_price = EXCLUDED.target_price,
                    order_status = EXCLUDED.order_status,
                    created_date = EXCLUDED.created_date,
                    order_execution_time = EXCLUDED.order_execution_time
            """, schema);
 
            jdbcTemplate.update(sql,
                asString(row.get("trading_order_details_id")),
                asString(row.get("client_id")),
                asString(row.get("rm_id")),
                asString(row.get("instrument_id")),
                asString(row.get("product_details_id")),
                asString(row.get("quantity")),
                parseDoubleOrNull(asString(row.get("price"))),
                asString(row.get("transaction_type")),
                parseDoubleOrNull(asString(row.get("stop_loss"))),
                parseDoubleOrNull(asString(row.get("target_price"))),
                asString(row.get("order_status")),
                toTimestamp(asString(row.get("created_date"))),
                toTimestamp(asString(row.get("order_execution_time")))
            );
        }
 
        
        
        
        
        
        
        
        @Override
        public void insertRecordingDetails(Map<String, Object> row) {
            // First, check if the record already exists
            String checkSql = String.format("SELECT COUNT(*) FROM %s.recording_details WHERE recording_details_id = ?", schema);
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, asString(row.get("recording_details_id")));
 
            if (count != null && count > 0) {
                // Record exists → update
                String updateSql = String.format(
                    "UPDATE %s.recording_details SET " +
                    "recording_id = ?, recording_name = ?, client_id = ?, rm_id = ?, " +
                    "trading_order_details_id = ?, recording_flag = ?, call_type = ?, " +
                    "start_time = ?, end_time = ?, created_date = ? " +
                    "WHERE recording_details_id = ?", schema
                );
 
                jdbcTemplate.update(updateSql,
                    asString(row.get("recording_id")),
                    asString(row.get("recording_name")),
                    asString(row.get("client_id")),
                    asString(row.get("rm_id")),
                    asString(row.get("trading_order_details_id")),
                    asString(row.get("recording_flag")),
                    asString(row.get("call_type")),
                    toTimestamp(asString(row.get("start_time"))),
                    toTimestamp(asString(row.get("end_time"))),
                    toTimestamp(asString(row.get("created_date"))),
                    asString(row.get("recording_details_id"))
                );
            } else {
                // Record does not exist → insert
                String insertSql = String.format(
                    "INSERT INTO %s.recording_details " +
                    "(recording_details_id, recording_id, recording_name, client_id, rm_id, " +
                    "trading_order_details_id, recording_flag, call_type, start_time, end_time, created_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", schema
                );
 
                jdbcTemplate.update(insertSql,
                    asString(row.get("recording_details_id")),
                    asString(row.get("recording_id")),
                    asString(row.get("recording_name")),
                    asString(row.get("client_id")),
                    asString(row.get("rm_id")),
                    asString(row.get("trading_order_details_id")),
                    asString(row.get("recording_flag")),
                    asString(row.get("call_type")),
                    toTimestamp(asString(row.get("start_time"))),
                    toTimestamp(asString(row.get("end_time"))),
                    toTimestamp(asString(row.get("created_date")))
                );
            }
        }
}
