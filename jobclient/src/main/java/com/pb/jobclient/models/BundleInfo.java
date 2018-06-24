package com.pb.jobclient.models;

import lombok.Data;

import java.io.File;

@Data
public class BundleInfo {
    File file;
    Company.Workflow workflow;
}
