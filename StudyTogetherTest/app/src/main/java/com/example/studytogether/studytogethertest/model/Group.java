package com.example.studytogether.studytogethertest.model;

public class Group {
    private String name;
    private Boolean active;
    private Integer numberOfMembers;
    private String goal;

    public Group() {
        // Empty Constructor needed for Firebase

    }

    public Group(String name, Boolean active, Integer numberOfMembers, String goal) {
        this.name = name;
        this.active = active;
        this.numberOfMembers = numberOfMembers;
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getNumberOfMembers() {
        return numberOfMembers;
    }

    public String getGoal() {
        return goal;
    }
}