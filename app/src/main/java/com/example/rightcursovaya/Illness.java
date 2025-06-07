package com.example.rightcursovaya;

public class Illness {
    private Long id;
    private String name;

    private String description;

    private String recommendation;

    public Illness(Long id, String name, String description, String recommendation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendation = recommendation;
    }

    public Illness() {
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRecommendation() {
        return recommendation;
    }
}