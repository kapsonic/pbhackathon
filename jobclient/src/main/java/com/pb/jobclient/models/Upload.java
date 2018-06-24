package com.pb.jobclient.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.Data;

import java.io.File;

@Data
public class Upload {
    public enum UploadStatus {
        IN_PROGRESS, FAILED, COMPLETED, FILE_MOVED;
    }

    @JsonIgnore
    private Number id;

    private String filename;
    private String key;
    private String jobName;
    private String userFullName;
    private String jobProfileId;

    @JsonIgnore
    private String path;

    private String identifier;
    private long totalSize;
    private String status;
    private String companyId;
    
    /**
     * This hash is used to detect if the file has changed locally since an
     * upload operation began. It is calculated based on the files path, length
     * and last modified date and does NOT include the content of the file.
     */
    @JsonIgnore
    private String hash;

    public Upload() {
    }

    public Upload(File file, String key) {
        this.key = key;

        filename = file.getName();
        int indx = filename.lastIndexOf(".");
        if(indx > 0) {
            jobName = filename.substring(0,indx);
        } else {
            jobName = filename;
        }
        path = file.getAbsolutePath();
        status = Upload.UploadStatus.IN_PROGRESS.toString();
        totalSize = file.length();

        hash = fileHash(file);
    }

    protected String fileHash(File file) {
        return Hashing.md5().hashString(String.format("%s-%d-%d", file.getAbsolutePath(), file.length(), file.lastModified()), Charsets.UTF_8).toString();
    }

    public boolean isForFile(File file) {
        return hash.equals(fileHash(file));
    }
}
