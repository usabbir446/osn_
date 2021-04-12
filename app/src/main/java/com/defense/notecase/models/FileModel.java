package com.defense.notecase.models;

public class FileModel {
    private String fileName;
    private String fileLink;
    private String uploadedBy;
    private String uploadDate;
    private String fileType;

    public FileModel()
    {
        this.fileName = null;
        this.fileLink = null;
        this.uploadedBy = null;
        this.uploadDate = null;
        this.fileType = null;
    }

    public FileModel(String fileName, String fileLink, String uploadedBy, String uploadDate, String fileType) {
        this.fileName = fileName;
        this.fileLink = fileLink;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
