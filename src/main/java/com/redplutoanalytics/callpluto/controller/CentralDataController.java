package com.redplutoanalytics.callpluto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redplutoanalytics.callpluto.service.CentralDataService;

@RestController
@RequestMapping("/api/centraldata")
public class CentralDataController {
	
	  @Autowired
	   CentralDataService service;
	 

	    @GetMapping("/departments")
	    public List<String> getDepartments() {
	        return service.getDepartments();
	    }

	    @GetMapping("/products")
	    public List<String> getProducts() {
	        return service.getProducts();
	    }

	    @GetMapping("/regions")
	    public List<String> getRegions() {
	        return service.getRegions();
	    }

	    @GetMapping("/rm-names")
	    public List<String> getRmNames() {
	        return service.getRmNames();
	    }
	}


