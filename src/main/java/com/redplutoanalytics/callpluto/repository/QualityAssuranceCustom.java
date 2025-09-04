package com.redplutoanalytics.callpluto.repository;

import java.util.Map;

import org.springframework.stereotype.Repository;



import java.util.Map;

public interface QualityAssuranceCustom {
    void insertQaValidationRecord(Map<String, Object> record);
}
