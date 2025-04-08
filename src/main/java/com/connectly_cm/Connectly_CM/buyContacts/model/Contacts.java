package com.connectly_cm.Connectly_CM.buyContacts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("contacts")
public class Contacts {
    @Id
    private String _id;
    private String name;
    private String email;
    private String phone;
    private String company_name;
    private String job_position;
    private String department;
    private int job_position_level;
    private Place place;
    private long company_annual_revenue;
    private long company_headcount;
    private String industry;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getJob_position() {
        return job_position;
    }

    public void setJob_position(String job_position) {
        this.job_position = job_position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getJob_position_level() {
        return job_position_level;
    }

    public void setJob_position_level(int job_position_level) {
        this.job_position_level = job_position_level;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public long getCompany_annual_revenue() {
        return company_annual_revenue;
    }

    public void setCompany_annual_revenue(long company_annual_revenue) {
        this.company_annual_revenue = company_annual_revenue;
    }

    public long getCompany_headcount() {
        return company_headcount;
    }

    public void setCompany_headcount(long company_headcount) {
        this.company_headcount = company_headcount;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
