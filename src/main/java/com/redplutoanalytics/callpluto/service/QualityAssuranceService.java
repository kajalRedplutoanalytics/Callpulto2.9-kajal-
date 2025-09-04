package com.redplutoanalytics.callpluto.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.redplutoanalytics.callpluto.repository.QualityAssuranceCustom;
import com.redplutoanalytics.callpluto.repository.QualityAssuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.QualityAssuranceRepository;
import com.redplutoanalytics.callpluto.repository.QualityAssuranceCustom;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.QualityAssuranceRepository;
import com.redplutoanalytics.callpluto.repository.QualityAssuranceCustom;

//@Service
//public class QualityAssuranceService {
// 
//    @Autowired
//    private QualityAssuranceRepository repo;
// 
//    private String getInterval(String timeFilter) {
//        return switch (timeFilter == null ? "" : timeFilter.toLowerCase()) {
//            case "month" -> "1 month";
//            case "day" -> "1 day";
//            default -> "7 days";
//        };
//    }
// 
//    /* Metrics endpoint */
//    public Map<String, Object> getQualityMetrics(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        long match = repo.countFiltered("match", interval, filters);
//        long mismatch = repo.countFiltered("mismatch", interval, filters);
//        long total = match + mismatch;
// 
//        return Map.of(
//            "totalCalls", Map.of("value", total, "change", 0),
//            "matchCount", Map.of("value", match, "change", 0),
//            "mismatchCount", Map.of("value", mismatch, "change", 0),
//            "avgProcessingTime", Map.of("value", "35.7", "change", 0)
//        );
//    }
// 
// 
//    public Map<String, Object> getMatchRate(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        Map<String, Object> result = repo.getMatchRateWithCounts(interval, filters);
//        return Map.of("matchRate", result);
//    }
// 
//    public Map<String, Object> getMatchMismatchComparison(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        long match = repo.countFiltered("match", interval, filters);
//        long mismatch = repo.countFiltered("mismatch", interval, filters);
//        return Map.of("match", match, "mismatch", mismatch);
//    }
// 
//    public List<Map<String, Object>> getMismatchByType(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Object[]> rows = repo.countMismatchByInstrument(interval, filters);
//        List<Map<String, Object>> output = new ArrayList<>();
//        for (Object[] row : rows) {
//            output.add(Map.of("type", row[0], "count", row[1]));
//        }
//        return output;
//    }
// 
//    public List<Map<String, Object>> getMatchMismatchTrend(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Object[]> rows = repo.getTrend(interval, filters);
//        List<Map<String, Object>> output = new ArrayList<>();
// 
//        for (Object[] row : rows) {
//            output.add(Map.of(
//                "date", row[0],
//                "match", row[1],
//                "mismatch", row[2],
//                "total", row[3]
//            ));
//        }
// 
//        return output;
//    }
// 
//    public Map<String, Object> getMatchAndMismatchRecords(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Map<String, Object>> matched = repo.fetchRecords("match", interval, filters);
//        List<Map<String, Object>> mismatched = repo.fetchRecords("mismatch", interval, filters);
//        return Map.of("matchRecords", matched, "mismatchRecords", mismatched);
//    }
//}
//@Service
//public class QualityAssuranceService {
// 
//    @Autowired
//    private QualityAssuranceRepository repo;
// 
//    private String getInterval(String timeFilter) {
//        return switch (timeFilter == null ? "" : timeFilter.toLowerCase()) {
//            case "month" -> "1 month";
//            case "day" -> "1 day";
//            default -> "7 days";
//        };
//    }
// 
//    /* Metrics endpoint */
//    public Map<String, Object> getQualityMetrics(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        long match = repo.countFiltered("match", interval, filters);
//        long mismatch = repo.countFiltered("mismatch", interval, filters);
//        long total = match + mismatch;
// 
//        return Map.of(
//            "totalCalls", Map.of("value", total, "change", 0),
//            "matchCount", Map.of("value", match, "change", 0),
//            "mismatchCount", Map.of("value", mismatch, "change", 0),
//            "avgProcessingTime", Map.of("value", "35.7", "change", 0)
//        );
//    }
// 
// 
//    public Map<String, Object> getMatchRate(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        Map<String, Object> result = repo.getMatchRateWithCounts(interval, filters);
//        return Map.of("matchRate", result);
//    }
// 
//    public Map<String, Object> getMatchMismatchComparison(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        long match = repo.countFiltered("match", interval, filters);
//        long mismatch = repo.countFiltered("mismatch", interval, filters);
//        return Map.of("match", match, "mismatch", mismatch);
//    }
// 
//    public List<Map<String, Object>> getMismatchByType(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Object[]> rows = repo.countMismatchByInstrument(interval, filters);
//        List<Map<String, Object>> output = new ArrayList<>();
//        for (Object[] row : rows) {
//            output.add(Map.of("type", row[0], "count", row[1]));
//        }
//        return output;
//    }
// 
//    public List<Map<String, Object>> getMatchMismatchTrend(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Object[]> rows = repo.getTrend(interval, filters);
//        List<Map<String, Object>> output = new ArrayList<>();
// 
//        for (Object[] row : rows) {
//            output.add(Map.of(
//                "date", row[0],
//                "match", row[1],
//                "mismatch", row[2],
//                "total", row[3]
//            ));
//        }
// 
//        return output;
//    }
// 
//    public Map<String, Object> getMatchAndMismatchRecords(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Map<String, Object>> matched = repo.fetchRecords("match", interval, filters);
//        List<Map<String, Object>> mismatched = repo.fetchRecords("mismatch", interval, filters);
//        return Map.of("matchRecords", matched, "mismatchRecords", mismatched);
//    }
// 
//	public List<Map<String, Object>> getPotpiByFlag(String timeFilter, Map<String, String> map) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}

@Service
public class QualityAssuranceService {

    @Autowired
    private QualityAssuranceRepository repo;

    private String getInterval(String timeFilter) {
        return switch (timeFilter == null ? "" : timeFilter.toLowerCase()) {
            case "month" -> "1 month";
            case "day" -> "1 day";
            default -> "7 days";
        };
    }

    // ✅ Metrics (no chart labels)
    public Map<String, Object> getQualityMetrics(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        long match = repo.countFiltered("match", interval, filters);
        long mismatch = repo.countFiltered("mismatch", interval, filters);
        long total = match + mismatch;

        return Map.of(
            "totalCalls", Map.of("value", total, "change", 0),
            "matchCount", Map.of("value", match, "change", 0),
            "mismatchCount", Map.of("value", mismatch, "change", 0),
            "avgProcessingTime", Map.of("value", "35.7", "change", 0)
        );
    }

    // ✅ Match rate (no chart labels)
    public Map<String, Object> getMatchRate(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        Map<String, Object> result = repo.getMatchRateWithCounts(interval, filters);
        return Map.of("matchRate", result);
    }

    // ✅ Match-Mismatch Comparison (Chart)
    public Map<String, Object> getMatchMismatchComparison(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        long match = repo.countFiltered("match", interval, filters);
        long mismatch = repo.countFiltered("mismatch", interval, filters);

        List<String> labels = List.of("Match", "Mismatch");
        List<Long> data = List.of(match, mismatch);

        Map<String, Object> dataset = Map.of("label", "Comparison", "data", data);

        return Map.of("labels", labels, "datasets", List.of(dataset));
    }

    // ✅ Mismatch By Type (Chart)
    public Map<String, Object> getMismatchByType(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        List<Object[]> rows = repo.countMismatchByInstrument(interval, filters);

        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add(row[0].toString());
            counts.add((Long) row[1]);
        }

        Map<String, Object> dataset = Map.of("label", "Mismatch By Type", "data", counts);
        return Map.of("labels", labels, "datasets", List.of(dataset));
    }

    // ✅ Match-Mismatch Trend (Chart)
//    public Map<String, Object> getMatchMismatchTrend(String timeFilter, Map<String, String> filters) {
//        String interval = getInterval(timeFilter);
//        List<Object[]> rows = repo.getTrend(interval, filters);
//
//        List<String> labels = new ArrayList<>();
//        List<Long> matchData = new ArrayList<>();
//        List<Long> mismatchData = new ArrayList<>();
//
//        for (Object[] row : rows) {
//            labels.add(row[0].toString());
//            matchData.add((Long) row[1]);
//            mismatchData.add((Long) row[2]);
//        }
//
//        Map<String, Object> dataset1 = Map.of("label", "Match", "data", matchData);
//        Map<String, Object> dataset2 = Map.of("label", "Mismatch", "data", mismatchData);
//
//        return Map.of("labels", labels, "datasets", List.of(dataset1, dataset2));
//    }
    public Map<String, Object> getMatchMismatchTrend(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        List<Object[]> rows = repo.getTrend(interval, filters);

        List<String> labels = new ArrayList<>();
        List<Long> matchData = new ArrayList<>();
        List<Long> mismatchData = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add(row[0].toString());
            matchData.add(((Number) row[1]).longValue());   // ✅ Safe conversion
            mismatchData.add(((Number) row[2]).longValue()); // ✅ Safe conversion
        }

        Map<String, Object> dataset1 = Map.of("label", "Match", "data", matchData);
        Map<String, Object> dataset2 = Map.of("label", "Mismatch", "data", mismatchData);

        return Map.of("labels", labels, "datasets", List.of(dataset1, dataset2));
    }
    public Map<String, Object> getMatchAndMismatchRecords(String timeFilter, Map<String, String> filters) {
        String interval = getInterval(timeFilter);
        List<Map<String, Object>> matched = repo.fetchRecords("match", interval, filters);
        List<Map<String, Object>> mismatched = repo.fetchRecords("mismatch", interval, filters);
        return Map.of("matchRecords", matched, "mismatchRecords", mismatched);
    }
}