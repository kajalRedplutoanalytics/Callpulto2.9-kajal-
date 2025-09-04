package com.redplutoanalytics.callpluto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redplutoanalytics.callpluto.model.CsatAndAgentScore;

@Repository
public interface CsatAndAgentScoreRepository extends JpaRepository<CsatAndAgentScore, Long> {
}