package com.redplutoanalytics.callpluto.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.redplutoanalytics.callpluto.service.DashboardService;

import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
 
    private final DashboardService service;
 
    public DashboardController(DashboardService service) {
        this.service = service;
    }
 
    @GetMapping("/metrics")
    public Map getMetrics(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        
        return service.getDashboardMetrics(filters.getTimeFilter(), filters.toMap());
    }
 
    @GetMapping("/match-ratio")
    public Map<String, Object> getMatchRatio(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        Map<String, Double> ratio = service.getMatchRatio(filters.getTimeFilter(), filters.toMap());
        Map<String, Object> response = new HashMap<>();
        // Always Match, Mismatch in that order
        response.put("data", Arrays.asList(
            ratio.getOrDefault("matchPercentage", 0.0),
            ratio.getOrDefault("mismatchPercentage", 0.0)
        ));
        response.put("labels", Arrays.asList("Match", "Mismatch"));
        return response;
    }
 
    @GetMapping("/top-agents")
    public Map<String, Object> getTopAgents(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        List<Map<String, Object>> agentStats = service.getTopAgents(filters.getTimeFilter(), filters.toMap());
        List<Double> data = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Map<String, Object> stat : agentStats) {
            data.add(((Number) stat.get("avgScore")).doubleValue());
            labels.add((String) stat.get("agentName"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("labels", labels);
        return response;
    }
 
    @GetMapping("/call-volume")
    public Map<String, Object> getCallVolume(@RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
        List<Map<String, Object>> volumeStats = service.getCallVolume(filters.getTimeFilter(), filters.toMap());
        List<Integer> data = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Map<String, Object> stat : volumeStats) {
            data.add((Integer) stat.get("count"));
            labels.add((String) stat.get("time"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("labels", labels);
        return response;
    }
}