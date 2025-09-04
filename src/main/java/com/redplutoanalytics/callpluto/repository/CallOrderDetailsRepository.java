package com.redplutoanalytics.callpluto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redplutoanalytics.callpluto.model.CallOrderDetails;

@Repository
public interface CallOrderDetailsRepository extends JpaRepository<CallOrderDetails, Long> {
}