package com.redplutoanalytics.callpluto.login.security;

import java.util.HashMap;
import java.util.Map;

import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;


public class FilterUtils {

    public static Map<String, String> toFilterMap(DashboardFilterDTO request) {
        Map<String, String> filters = new HashMap<>();
        if (request.getDepartment() != null) filters.put("department", request.getDepartment());
        if (request.getRmName() != null) filters.put("rm_name", request.getRmName());
        if (request.getProduct() != null) filters.put("product", request.getProduct());
        if (request.getRegion() != null) filters.put("region", request.getRegion());
        return filters;
    }


}
