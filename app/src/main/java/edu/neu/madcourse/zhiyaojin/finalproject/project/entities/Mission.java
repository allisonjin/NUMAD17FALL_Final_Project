package edu.neu.madcourse.zhiyaojin.finalproject.project.entities;

public class Mission {

    private String missionId;
    private String userId;
    private String createdTime;
    private String completedTime;
    private String imageURL;
    private String thumbURL;

    private double latitude;
    private double longitude;
    private boolean completed;

    public Mission() {

    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbnailURL) {
        this.thumbURL = thumbnailURL;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mission mission = (Mission) o;

        return missionId != null ? missionId.equals(mission.missionId) : mission.missionId == null;
    }

    @Override
    public int hashCode() {
        return imageURL != null ? imageURL.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "userId='" + userId + '\'' +
                ", createdTime=" + createdTime +
                ", imageURL='" + imageURL + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", completed=" + completed +
                '}';
    }
}
