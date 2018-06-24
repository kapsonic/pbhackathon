package com.pb.jobclient.models;

import com.pb.jobclient.models.Company.Workflow;
import lombok.Data;

import java.io.File;

@Data
public class ActiveUpload {

    private Upload upload;
    private File file;
    private Workflow workflow;

    public ActiveUpload() {
    }

    public ActiveUpload(Upload upload, File file, Workflow workflow) {
        this.upload = upload;
        this.file = file;
        this.workflow = workflow;
    }
}
