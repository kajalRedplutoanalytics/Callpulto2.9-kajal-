package com.redplutoanalytics.callpluto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redplutoanalytics.callpluto.repository.CentraDataRepositoryCustom;

@Service
public class CentralDataService {

	@Autowired
	private CentraDataRepositoryCustom repository;

	public List<String> getDepartments() {
		return repository.getDepartments();
	}

	public List<String> getProducts() {
		return repository.getProducts();
	}

	public List<String> getRegions() {
		return repository.getRegions();
	}

	public List<String> getRmNames() {
		return repository.getRmNames();
	}

}
