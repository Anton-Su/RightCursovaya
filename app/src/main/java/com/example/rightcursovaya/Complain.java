package com.example.rightcursovaya;

public class Complain {
    private Long id;
    private Long doctor_id;
    private String complaint;

    public Complain(Long id, Long doctor_id, String complaint) {
        this.id = id;
        this.doctor_id = doctor_id;
        this.complaint = complaint;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public Long getId() {
        return id;
    }

    public Long getDoctor_id() {
        return doctor_id;
    }

    public String getComplaint() {
        return complaint;
    }
}