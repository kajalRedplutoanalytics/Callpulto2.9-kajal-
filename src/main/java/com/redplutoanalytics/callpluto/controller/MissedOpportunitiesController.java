package com.redplutoanalytics.callpluto.controller;
 
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import com.redplutoanalytics.callpluto.service.MissedOpportunitiesService;
 


@RestController
@RequestMapping("/api/missedopportunities")
@CrossOrigin
public class MissedOpportunitiesController {
 
    @Autowired
    private MissedOpportunitiesService service;
 
    @GetMapping("/missed-opportunities")
    public ResponseEntity<Map<String, Object>> getMissedOpportunityStats(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
 
        Map<String, Object> rawStats = service.getMissedOpportunityStats(filters.getTimeFilter(), filters.toMap());
 
        Map<String, Object> response = Map.of(
                "revenue", Map.of(
                        "value", rawStats.getOrDefault("revenue", 0),
                        "change", rawStats.getOrDefault("revenueChange", 0)
                ),
                "totalMissed", Map.of(
                        "value", rawStats.getOrDefault("totalMissed", 0),
                        "change", rawStats.getOrDefault("totalMissedChange", 0)
                )
        );
 
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/missed-opportunities/types")
    public ResponseEntity<Map<String, Object>> getOpportunityTypes(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {
 
        List<Map<String, Object>> types = service.getOpportunityTypes(filters.getTimeFilter(), filters.toMap());
 
        List<String> labels = types.stream()
                .map(type -> (String) type.get("type"))
                .collect(Collectors.toList());
 
        List<Number> data = types.stream()
                .map(type -> (Number) type.get("count"))
                .collect(Collectors.toList());
 
        Map<String, Object> response = Map.of(
                "opportunityTypes", Map.of(
                        "labels", labels,
                        "datasets", List.of(
                                Map.of("data", data)
                        )
                )
        );
 
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/missed-opportunities/agents")
    public ResponseEntity<Map<String, Object>> getMissedByAgent(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {

        Map<String, Object> agentData = service.getMissedByAgent(filters.getTimeFilter(), filters.toMap());

      
        return ResponseEntity.ok(Map.of("opportunitiesByAgent", agentData));
    }

 
    @GetMapping("/missed-opportunities/trend")
    public ResponseEntity<Map<String, Object>> getMissedOpportunityTrend(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters) {

        // Service now returns chart-ready Map<String, Object>
        Map<String, Object> trendData = service.getMissedOpportunityTrend(filters.getTimeFilter(), filters.toMap());

        // Directly return it
        return ResponseEntity.ok(Map.of("opportunityTrend", trendData));
    }
    
    @GetMapping("/top-agents")
    public ResponseEntity<Map<String, Object>> getTopAgentsByType(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
            @RequestParam(name = "type", defaultValue = "revenue") String type) {

        List<Map<String, Object>> data;

        if ("revenue".equalsIgnoreCase(type)) {
            data = service.getTopAgentsByMissedRevenue(filters.getTimeFilter(), filters.toMap());
        } else if ("commission".equalsIgnoreCase(type)) {
            data = service.getTopAgentsByMissedCommission(filters.getTimeFilter(), filters.toMap());
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid type. Use 'revenue' or 'commission'."
            ));
        }

        Map<String, Object> response = Map.of(
                "cardTitle", "Top Agents by Missed " + type.substring(0, 1).toUpperCase() + type.substring(1),
                "agents", data
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductRatio(
            @RequestAttribute("dashboardFilters") DashboardFilterDTO filters,
            @RequestParam(name = "type", defaultValue = "commission") String type) {

        List<Map<String, Object>> products;

        if ("commission".equalsIgnoreCase(type)) {
            products = service.getProductCommissionRevenueRatio(filters.getTimeFilter(), filters.toMap());
        } else if ("revenue".equalsIgnoreCase(type)) {
            products = service.getProductCommissionRevenueRatio(filters.getTimeFilter(), filters.toMap());
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid type. Use 'commission' or 'revenue'."
            ));
        }

        Map<String, Object> response = Map.of(
                "cardTitle", "Product " + type + "",
                "products", products
        );

        return ResponseEntity.ok(response);
    }


}