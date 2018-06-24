package com.pb.jobclient.models;

import java.util.Date;

public class FileInfo {
    
    
    private String customerFileName;

    private String uploadedFileName;
    
    private Date dateCompleted;
    
    public String getUploadedFileName() {
        return uploadedFileName;
    }
    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }
    public String getCustomerFileName() {
        return customerFileName;
    }
    public void setCustomerFileName(String customerFileName) {
        this.customerFileName = customerFileName;
    }
    public Date getDateCompleted() {
        return dateCompleted;
    }
    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
    
    
}