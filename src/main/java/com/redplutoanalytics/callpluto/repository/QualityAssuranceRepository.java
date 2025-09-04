package com.redplutoanalytics.callpluto.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.redplutoanalytics.callpluto.model.OrderRecords;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.redplutoanalytics.callpluto.model.OrderRecords;

@Repository
public interface QualityAssuranceRepository {

    long countFiltered(String status, String interval, Map<String, String> filters);

    List<Object[]> countMismatchByInstrument(String interval, Map<String, String> filters);

    List<Object[]> getTrend(String interval, Map<String, String> filters);

    List<Map<String, Object>> fetchRecords(String status, String interval, Map<String, String> filters);
    Map<String, Object> getMatchRateWithCounts(String interval, Map<String, String> filters);
}