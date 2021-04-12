package com.defense.notecase.models;

public class NotificationModel {
    private String date;
    private String recordType;
    private String uploadedBy;
    private String fileLink;
    private String notificationBody;

    public NotificationModel()
    {
        this.date = null;
        this.recordType = null;
        this.uploadedBy = null;
        this.fileLink = null;
        this.notificationBody = null;
    }
    public NotificationModel(String date, String recordType, String uploadedBy, String fileLink, String notificationBody) {
        this.date = date;
        this.recordType = recordType;
        this.uploadedBy = uploadedBy;
        this.fileLink = fileLink;
        this.notificationBody = notificationBody;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }
}
