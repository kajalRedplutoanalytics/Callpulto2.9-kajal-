package com.redplutoanalytics.callpluto.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import com.redplutoanalytics.callpluto.service.AgentPerformanceService;


//@RestController
//@RequestMapping("/api/agentperformance")
//@CrossOrigin
//public class AgentPerformanceController {
//
//	@Autowired
//    private AgentPerformanceService service;
//
//    @GetMapping("/agent-performance")
//    public ResponseEntity<Map<String, Object>> getAgentPerformance(
//            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        Map<String, Object> result = service.getAgentPerformance(filters.getTimeFilter(), filters.toMap());
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/agent-positive-negative-words")
//    public ResponseEntity<List<Map<String, Object>>> getAgentPositiveNegativeWords(
//            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
//        List<Map<String, Object>> words = service.getAgentPositiveNegativeWords(filters.getTimeFilter(), filters.toMap());
//        return ResponseEntity.ok(words);
//    }
//}

@RestController
@RequestMapping("/api/agentperformance")
@CrossOrigin
public class AgentPerformanceController {
 
	@Autowired
    private AgentPerformanceService service;
	
	@GetMapping("/agent-performance")
	public ResponseEntity<Map<String, Object>> getAgentPerformance(
	        @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
 
	    Map<String, Object> result = service.getAgentPerformance(filters.getTimeFilter(), filters.toMap());
 
	    List<String> labels = new ArrayList<>();
	    List<Number> data = new ArrayList<>();
 
	    for (Map.Entry<String, Object> entry : result.entrySet()) {
	        labels.add(entry.getKey());
	        Object value = entry.getValue();
	        if (value instanceof Number) {
	            data.add((Number) value);
	        } else {
	            data.add(0); // fallback
	        }
	    }
 
	    Map<String, Object> dataset = new HashMap<>();
	    dataset.put("label", "Agent Performance");
	    dataset.put("data", data);
	    dataset.put("yAxisID", "y");
 
	    Map<String, Object> response = new HashMap<>();
	    response.put("labels", labels);
	    response.put("datasets", List.of(dataset));
 
	    return ResponseEntity.ok(response);
	}
 
    
    
    @GetMapping("/agent-positive-negative-words")
    public ResponseEntity<Map<String, Object>> getAgentPositiveNegativeWords(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
 
        List<Map<String, Object>> words = service.getAgentPositiveNegativeWords(filters.getTimeFilter(), filters.toMap());
 
        List<String> agentNames = new ArrayList<>();
        List<Integer> positiveCounts = new ArrayList<>();
        List<Integer> negativeCounts = new ArrayList<>();
 
        for (Map<String, Object> row : words) {
            agentNames.add((String) row.get("rmName"));
 
            List<String> positives = (List<String>) row.get("positiveWords");
            List<String> negatives = (List<String>) row.get("negativeWords");
 
            positiveCounts.add(positives != null ? positives.size() : 0);
            negativeCounts.add(negatives != null ? negatives.size() : 0);
        }
 
        Map<String, Object> positiveDataset = new HashMap<>();
        positiveDataset.put("label", "Positive Words");
        positiveDataset.put("data", positiveCounts);
 
        Map<String, Object> negativeDataset = new HashMap<>();
        negativeDataset.put("label", "Negative Words");
        negativeDataset.put("data", negativeCounts);
 
        Map<String, Object> response = new HashMap<>();
        response.put("labels", agentNames);
        response.put("datasets", List.of(positiveDataset, negativeDataset));
 
        return ResponseEntity.ok(response);
    }
 
    
    private List<String> splitWords(String input) {
        if (input == null || input.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}