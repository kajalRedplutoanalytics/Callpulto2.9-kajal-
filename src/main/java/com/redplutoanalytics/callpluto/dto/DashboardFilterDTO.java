package com.redplutoanalytics.callpluto.dto;

import java.util.HashMap;
import java.util.Map;

public class DashboardFilterDTO {
    private String timeFilter = "week";
    private String department;
    private String rm_name;
    private String product;
    private String region;
    private String call_type;

    private Boolean mismatchOnly = false;

    public Boolean getMismatchOnly() {
        return mismatchOnly;
    }

    public void setMismatchOnly(Boolean mismatchOnly) {
        this.mismatchOnly = mismatchOnly != null ? mismatchOnly : false;
    }

    public String getTimeFilter() {
        return timeFilter;
    }

    public void setTimeFilter(String timeFilter) {
        if (timeFilter != null)
            this.timeFilter = timeFilter;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRmName() {
        return rm_name;
    }

    public void setRmName(String rmName) {
        this.rm_name = rmName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCallType() {
        return call_type;
    }

    public void setCallType(String callType) {
        this.call_type = callType;
    }

    // Convert to repository-compatible map
 // Existing method - keep as it is for dashboard
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (department != null)
            map.put("department", department);
        if (rm_name != null)
            map.put("rm_name", rm_name); // repository expects rm_name
        if (product != null)
            map.put("product", product);
        if (region != null)
            map.put("region", region);
        if (call_type != null)
            map.put("call_type", call_type); // repository expects call_type
        return map;
    }

    // ✅ New method for AudioLibrary with mismatch support
    public Map<String, Object> toAudioLibraryMap() {
        Map<String, Object> map = new HashMap<>();
        if (department != null)
            map.put("department", department);
        if (rm_name != null)
            map.put("rm_name", rm_name);
        if (product != null)
            map.put("product", product);
        if (region != null)
            map.put("region", region);
        if (call_type != null)
            map.put("call_type", call_type);

        // Add mismatch flag only if true
        if (Boolean.TRUE.equals(mismatchOnly)) {
            map.put("mismatch", true);
        }

        return map;
    }

}
