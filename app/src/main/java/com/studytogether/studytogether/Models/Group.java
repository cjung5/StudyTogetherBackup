package com.studytogether.studytogether.Models;

import com.google.firebase.database.ServerValue;

public class Group {

    // All attributes
    private String groupName;
    private String groupGoal;
    private String groupPlace;
    private String num_of_group_members;
    private String startTime;
    private String endTime;
    private String groupPicture;
    private String groupKey;
    private String ownerId;
    private String groupOwnerPhoto;
    private String groupActive;
    private String tutor;
    private Object timeStamp;

    // Constructor
    public Group(String groupName, String groupGoal, String groupPlace, String tutor, String num_of_group_members, String startTime, String endTime, String groupPicture, String ownerId, String groupOwnerPhoto) {
        this.groupName = groupName;
        this.groupGoal = groupGoal;
        this.groupPlace = groupPlace;
        this.tutor = tutor;
        this.num_of_group_members = num_of_group_members;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groupPicture = groupPicture;
        this.ownerId = ownerId;
        this.groupOwnerPhoto = groupOwnerPhoto;
        this.groupActive = "Open";
        this.timeStamp = ServerValue.TIMESTAMP;
    }
    public Group() {
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupGoal() {
        return groupGoal;
    }

    public String getGroupPlace() {
        return groupPlace;
    }

    public String getNum_of_group_members() {
        return num_of_group_members;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getGroupPicture() {
        return groupPicture;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getGroupOwnerPhoto() {
        return groupOwnerPhoto;
    }

    public String getGroupActive() {
        return groupActive;
    }

    public String getTutor() {
        return tutor;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupGoal(String groupGoal) {
        this.groupGoal = groupGoal;
    }

    public void setGroupPlace(String groupPlace) {
        this.groupPlace = groupPlace;
    }

    public void setNum_of_group_members(String num_of_group_members) {
        this.num_of_group_members = num_of_group_members;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setGroupPicture(String groupPicture) {
        this.groupPicture = groupPicture;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setGroupOwnerPhoto(String groupOwnerPhoto) {
        this.groupOwnerPhoto = groupOwnerPhoto;
    }

    public void setGroupActive(String groupActive) {
        this.groupActive = groupActive;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
