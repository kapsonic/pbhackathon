package com.pb.jobclient.models;

import lombok.Data;

import java.util.List;

@Data
public class Session {
    private int errorCode = 0;
    private String errorMessage;
    private String id;
    private String fullName;
    private List<String> roles;
    private String companyId;
    private String csrf;
}