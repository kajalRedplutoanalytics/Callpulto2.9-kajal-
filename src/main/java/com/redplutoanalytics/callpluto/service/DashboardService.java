package com.redplutoanalytics.callpluto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.DashboardRepository;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public static class DateRange {
        public final LocalDateTime start, end;
        public final String interval;

        public DateRange(LocalDateTime start, LocalDateTime end, String interval) {
            this.start = start;
            this.end = end;
            this.interval = interval;
        }
    }

    public DateRange getDateRange(String timeFilter) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeFilter == null ? "" : timeFilter.toLowerCase()) {
            case "today":
                return new DateRange(now.withHour(0).withMinute(0).withSecond(0).withNano(0), now, "hour");
            case "yesterday":
                LocalDateTime yest = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                return new DateRange(yest, yest.plusHours(23).plusMinutes(59).plusSeconds(59), "hour");
            case "week":
                return new DateRange(now.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0), now, "day");
            case "month":
                return new DateRange(now.minusDays(29).withHour(0).withMinute(0).withSecond(0).withNano(0), now, "week");
            default:
                return new DateRange(now.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0), now, "day");
        }
    }

    public Map<String, Object> getDashboardMetrics(String timeFilter, Map<String, String> filters) {
        DateRange dr = getDateRange(timeFilter);
        DashboardRepository.Metrics current = repository.getKpiMetrics(dr.start, dr.end, filters);
        long days = java.time.Duration.between(dr.start, dr.end).toDays();
        LocalDateTime prevStart = dr.start.minusDays(days > 0 ? days : 1);
        LocalDateTime prevEnd = dr.start;
        DashboardRepository.Metrics previous = repository.getKpiMetrics(prevStart, prevEnd, filters);

        double totalCallsChange = (previous.totalCalls == 0) ? 0 : ((current.totalCalls - previous.totalCalls) / (double) previous.totalCalls) * 100.0;
        double matchCountChange = (previous.matchCount == 0) ? 0 : ((current.matchCount - previous.matchCount) / (double) previous.matchCount) * 100.0;
        double mismatchCountChange = (previous.mismatchCount == 0) ? 0 : ((current.mismatchCount - previous.mismatchCount) / (double) previous.mismatchCount) * 100.0;

        Map<String, Object> csat = repository.getCSATScore(dr.start, dr.end, prevStart, prevEnd, filters);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCalls", Map.of("value", String.valueOf(current.totalCalls), "change", Math.round(totalCallsChange * 10.0) / 10.0));
        result.put("matchCount", Map.of("value", String.valueOf(current.matchCount), "change", Math.round(matchCountChange * 10.0) / 10.0));
        result.put("mismatchCount", Map.of("value", String.valueOf(current.mismatchCount), "change", Math.round(mismatchCountChange * 10.0) / 10.0));
        result.put("csatScore", csat);
        return result;
    }

    public Map<String, Double> getMatchRatio(String timeFilter, Map<String, String> filters) {
        DateRange dr = getDateRange(timeFilter);
        return repository.getMatchRatio(dr.start, dr.end, filters);
    }

    public List<Map<String, Object>> getTopAgents(String timeFilter, Map<String, String> filters) {
        DateRange dr = getDateRange(timeFilter);
        return repository.getTopAgents(dr.start, dr.end, filters);
    }

    public List<Map<String, Object>> getCallVolume(String timeFilter, Map<String, String> filters) {
        DateRange dr = getDateRange(timeFilter);
        return repository.getCallVolume(dr.start, dr.end, dr.interval, filters);
    }
}