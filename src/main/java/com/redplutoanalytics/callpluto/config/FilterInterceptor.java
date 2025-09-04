package com.redplutoanalytics.callpluto.config;

import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FilterInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		DashboardFilterDTO filters = new DashboardFilterDTO();
		filters.setTimeFilter(request.getParameter("timeFilter") == null ? "week" : request.getParameter("timeFilter"));
		filters.setDepartment(request.getParameter("department"));
		filters.setRmName(request.getParameter("rm_name"));
		filters.setProduct(request.getParameter("product"));
		filters.setRegion(request.getParameter("region"));
		filters.setCallType(request.getParameter("call_type"));
		request.setAttribute("dashboardFilters", filters);
		return true;
	}
}
