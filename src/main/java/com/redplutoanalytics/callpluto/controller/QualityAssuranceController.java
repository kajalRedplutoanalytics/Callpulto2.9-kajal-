package com.redplutoanalytics.callpluto.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import com.redplutoanalytics.callpluto.service.QualityAssuranceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//@RestController
//@RequestMapping("/api/qualityassurance")
//public class QualityAssuranceController {
//
//    @Autowired
//    private QualityAssuranceService service;
//    @GetMapping("/metrics")
//    public ResponseEntity<Map<String, Object>> getMetrics(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//       return ResponseEntity.ok(
//            service.getQualityMetrics(filters.getTimeFilter(), filters.toMap())
//       );
//    }
//
//    @GetMapping("/match-rate")
//    public ResponseEntity<Map<String, Object>> getMatchRate(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(
//            service.getMatchRate(filters.getTimeFilter(), filters.toMap())
//        );
//    }
//
//    @GetMapping("/match-mismatch-comparison")
//    public ResponseEntity<Map<String, Object>> getMatchMismatchComparison(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(
//            service.getMatchMismatchComparison(filters.getTimeFilter(), filters.toMap())
//        );
//    }
//
//    @GetMapping("/mismatch-by-type")
//    public ResponseEntity<List<Map<String, Object>>> getMismatchByType(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(
//            service.getMismatchByType(filters.getTimeFilter(), filters.toMap())
//        );
//    }
//
//    @GetMapping("/match-mismatch-trend")
//    public ResponseEntity<List<Map<String, Object>>> getMatchMismatchTrend(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(
//            service.getMatchMismatchTrend(filters.getTimeFilter(), filters.toMap())
//        );
//    }
//
//    @GetMapping("/result-records")
//    public ResponseEntity<Map<String, Object>> getResultRecords(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
//                                                                @RequestParam(required = false) String callType) {
//        Map<String, String> map = filters.toMap();
//        if (callType != null) map.put("call_type", callType);
//
//        return ResponseEntity.ok(
//            service.getMatchAndMismatchRecords(filters.getTimeFilter(), map)
//        );
//    }
//}

//@RestController
//@RequestMapping("/api/qualityassurance")
//public class QualityAssuranceController {
//
//    @Autowired
//    private QualityAssuranceService service;
//
//    @GetMapping("/metrics")
//    public ResponseEntity<Map<String, Object>> getMetrics(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        // Direct pass-through since metrics already likely return key-values
//        return ResponseEntity.ok(service.getQualityMetrics(filters.getTimeFilter(), filters.toMap()));
//    }
//
//    @GetMapping("/match-rate")
//    public ResponseEntity<Map<String, Object>> getMatchRate(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        Map<String, Object> rawData = service.getMatchRate(filters.getTimeFilter(), filters.toMap());
//
//        // Assuming rawData contains {matchRate: x, mismatchRate: y}
//        Map<String, Object> chart = new HashMap<>();
//        chart.put("labels", List.of("Match Rate", "Mismatch Rate"));
//        chart.put("data", List.of(rawData.get("matchRate"), rawData.get("mismatchRate")));
//
//        return ResponseEntity.ok(chart);
//    }
//
//    @GetMapping("/match-mismatch-comparison")
//    public ResponseEntity<Map<String, Object>> getMatchMismatchComparison(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        Map<String, Object> rawData = service.getMatchMismatchComparison(filters.getTimeFilter(), filters.toMap());
//
//        // Expected: rawData = {labels: [...], match: [...], mismatch: [...]}
//        Map<String, Object> chart = new HashMap<>();
//        chart.put("labels", rawData.get("labels"));
//
//        List<Map<String, Object>> datasets = new ArrayList<>();
//        datasets.add(Map.of("label", "Match", "data", rawData.get("match")));
//        datasets.add(Map.of("label", "Mismatch", "data", rawData.get("mismatch")));
//
//        chart.put("datasets", datasets);
//        return ResponseEntity.ok(chart);
//    }
//
//    @GetMapping("/mismatch-by-type")
//    public ResponseEntity<Map<String, Object>> getMismatchByType(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        List<Map<String, Object>> rawData = service.getMismatchByType(filters.getTimeFilter(), filters.toMap());
//
//        // Transform to labels + data
//        List<String> labels = new ArrayList<>();
//        List<Object> data = new ArrayList<>();
//        for (Map<String, Object> row : rawData) {
//            labels.add(row.get("type").toString());
//            data.add(row.get("count"));
//        }
//
//        return ResponseEntity.ok(Map.of("labels", labels, "data", data));
//    }
//
//    @GetMapping("/match-mismatch-trend")
//    public ResponseEntity<Map<String, Object>> getMatchMismatchTrend(
//            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//
//        List<Map<String, Object>> rawData = service.getMatchMismatchTrend(filters.getTimeFilter(), filters.toMap());
//
//        List<String> labels = new ArrayList<>();
//        List<Integer> matchData = new ArrayList<>();
//        List<Integer> mismatchData = new ArrayList<>();
//
//        for (Map<String, Object> row : rawData) {
//            labels.add(row.get("date").toString());
//            matchData.add((Integer) row.getOrDefault("match", 0));
//            mismatchData.add((Integer) row.getOrDefault("mismatch", 0));
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("labels", labels);
//        response.put("datasets", List.of(
//                Map.of("label", "Match", "data", matchData),
//                Map.of("label", "Mismatch", "data", mismatchData)
//        ));
//
//        return ResponseEntity.ok(response);
//    }
//
//
//    @GetMapping("/result-records")
//    public ResponseEntity<Map<String, Object>> getResultRecords(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
//                                                                @RequestParam(required = false) String callType) {
//        Map<String, String> map = filters.toMap();
//        if (callType != null) map.put("call_type", callType);
//
//        Map<String, Object> rawData = service.getMatchAndMismatchRecords(filters.getTimeFilter(), map);
//
//        // Assuming rawData contains "records" as a list
//        List<Map<String, Object>> records = (List<Map<String, Object>>) rawData.get("records");
//
//        List<String> labels = new ArrayList<>();
//        List<Object> data = new ArrayList<>();
//        for (Map<String, Object> record : records) {
//            labels.add(record.get("callType") + " - " + record.get("status"));
//            data.add(1); // each record counts as one occurrence
//        }
//
//        return ResponseEntity.ok(Map.of("labels", labels, "data", data));
//    }
//}
//@RestController
//@RequestMapping("/api/qualityassurance")
//public class QualityAssuranceController {
//
//    @Autowired
//    private QualityAssuranceService service;
//
//    // ✅ Keep metrics unchanged
//    @GetMapping("/metrics")
//    public ResponseEntity<Map<String, Object>> getMetrics(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(service.getQualityMetrics(filters.getTimeFilter(), filters.toMap()));
//    }
//
//    // ✅ Keep match-rate unchanged
//    @GetMapping("/match-rate")
//    public ResponseEntity<Map<String, Object>> getMatchRate(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        return ResponseEntity.ok(service.getMatchRate(filters.getTimeFilter(), filters.toMap()));
//    }
//
//    // ❌ Remove xAxisLabel / yAxisLabel
//    @GetMapping("/match-mismatch-comparison")
//    public ResponseEntity<Map<String, Object>> getMatchMismatchComparison(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        Map<String, Object> raw = service.getMatchMismatchComparison(filters.getTimeFilter(), filters.toMap());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("datasets", raw.get("datasets"));
//        response.put("labels", raw.get("labels"));
//        response.put("showDataLabels", false);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ❌ Remove xAxisLabel / yAxisLabel
//    @GetMapping("/mismatch-by-type")
//    public ResponseEntity<?> getMismatchByType(@RequestAttribute DashboardFilterDTO filters) {
//        List<Map<String, Object>> result = (List<Map<String, Object>>)
//                service.getMismatchByType(filters.getTimeFilter(), filters.toMap());
//
//        List<String> labels = result.stream()
//                .map(row -> row.get("label").toString())
//                .collect(Collectors.toList());
//
//        List<Number> data = result.stream()
//                .map(row -> (Number) row.get("value"))
//                .collect(Collectors.toList());
//
//        Map<String, Object> dataset = new HashMap<>();
//        dataset.put("label", "Mismatch By Type");
//        dataset.put("data", data);
//        dataset.put("yAxisID", "y");
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("labels", labels);
//        response.put("datasets", List.of(dataset));
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ❌ Remove xAxisLabel / yAxisLabel
//    @GetMapping("/match-mismatch-trend")
//    public ResponseEntity<Map<String, Object>> getMatchMismatchTrend(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        List<Map<String, Object>> rawList = service.getMatchMismatchTrend(filters.getTimeFilter(), filters.toMap());
//
//        List<String> labels = rawList.stream()
//            .map(item -> String.valueOf(item.get("date")))
//            .distinct()
//            .toList();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("datasets", rawList);
//        response.put("labels", labels);
//        response.put("showDataLabels", false);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ✅ Records API unchanged (no x/y labels here anyway)
//    @GetMapping("/result-records")
//    public ResponseEntity<Map<String, Object>> getResultRecords(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
//                                                                @RequestParam(required = false) String callType) {
//        Map<String, String> map = filters.toMap();
//        if (callType != null) map.put("call_type", callType);
//
//        return ResponseEntity.ok(service.getMatchAndMismatchRecords(filters.getTimeFilter(), map));
//    }
//
// 
//    @GetMapping("/potpi-by-flag")
//    public ResponseEntity<?> getPotpiByFlag(@RequestAttribute DashboardFilterDTO filters) {
//        List<Map<String, Object>> result = (List<Map<String, Object>>)
//                service.getPotpiByFlag(filters.getTimeFilter(), filters.toMap());
//
//        List<String> labels = result.stream()
//                .map(row -> row.get("label").toString())
//                .collect(Collectors.toList());
//
//        List<Number> data = result.stream()
//                .map(row -> (Number) row.get("value"))
//                .collect(Collectors.toList());
//
//        Map<String, Object> dataset = new HashMap<>();
//        dataset.put("label", "POTPI By Flag");
//        dataset.put("data", data);
//        dataset.put("yAxisID", "y");
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("labels", labels);
//        response.put("datasets", List.of(dataset));
//
//        return ResponseEntity.ok(response);
//    }
//}
@RestController
@RequestMapping("/api/qualityassurance")
public class QualityAssuranceController {

    @Autowired
    private QualityAssuranceService service;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        return ResponseEntity.ok(service.getQualityMetrics(filters.getTimeFilter(), filters.toMap()));
    }

    @GetMapping("/match-rate")
    public ResponseEntity<Map<String, Object>> getMatchRate(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        return ResponseEntity.ok(service.getMatchRate(filters.getTimeFilter(), filters.toMap()));
    }

    @GetMapping("/match-mismatch-comparison")
    public ResponseEntity<Map<String, Object>> getMatchMismatchComparison(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        return ResponseEntity.ok(service.getMatchMismatchComparison(filters.getTimeFilter(), filters.toMap()));
    }

    @GetMapping("/mismatch-by-type")
    public ResponseEntity<Map<String, Object>> getMismatchByType(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        return ResponseEntity.ok(service.getMismatchByType(filters.getTimeFilter(), filters.toMap()));
    }

    @GetMapping("/match-mismatch-trend")
    public ResponseEntity<Map<String, Object>> getMatchMismatchTrend(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        return ResponseEntity.ok(service.getMatchMismatchTrend(filters.getTimeFilter(), filters.toMap()));
    }

    @GetMapping("/result-records")
    public ResponseEntity<Map<String, Object>> getResultRecords(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
                                                                @RequestParam(required = false) String callType) {
        Map<String, String> map = filters.toMap();
        if (callType != null) map.put("call_type", callType);

        return ResponseEntity.ok(service.getMatchAndMismatchRecords(filters.getTimeFilter(), map));
    }
}
