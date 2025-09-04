package com.redplutoanalytics.callpluto.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CentraDataRepositoryCustom  {

	List<String> getDepartments();

	List<String> getProducts();

	List<String> getRegions();

	List<String> getRmNames();
}
