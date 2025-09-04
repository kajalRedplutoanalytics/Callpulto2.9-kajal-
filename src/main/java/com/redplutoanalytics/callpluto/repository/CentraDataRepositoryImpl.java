package com.redplutoanalytics.callpluto.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CentraDataRepositoryImpl implements CentraDataRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final String schema;

    public CentraDataRepositoryImpl(JdbcTemplate jdbcTemplate,
                                    @Value("${spring.jpa.properties.hibernate.default_schema}") String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.schema = schema;
    }

    @Override
    public List<String> getDepartments() {
        return jdbcTemplate.query(
            "SELECT DISTINCT dept_name FROM " + schema + ".department_details ORDER BY dept_name",
            (rs, rowNum) -> rs.getString("dept_name"));
    }

    @Override
    public List<String> getProducts() {
        return jdbcTemplate.query(
            "SELECT DISTINCT product_name FROM " + schema + ".product_details ORDER BY product_name",
            (rs, rowNum) -> rs.getString("product_name"));
    }

    @Override
    public List<String> getRegions() {
        return jdbcTemplate.query(
            "SELECT DISTINCT state FROM " + schema + ".location_details ORDER BY state",
            (rs, rowNum) -> rs.getString("state"));
    }

    @Override
    public List<String> getRmNames() {
        return jdbcTemplate.query(
            "SELECT DISTINCT first_name || ' ' || last_name AS full_name FROM " + schema + ".employee_details ORDER BY full_name",
            (rs, rowNum) -> rs.getString("full_name"));
    }
}
