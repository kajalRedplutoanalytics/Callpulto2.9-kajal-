package com.redplutoanalytics.callpluto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
	@Autowired
	private FilterInterceptor FilterInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(FilterInterceptor).addPathPatterns("/api/dashboard/**", "/api/agentperformance/**",
				"/api/missedopportunities/**", "/api/centraldata/**", "/api/qualityassurance/**");
	}
}
