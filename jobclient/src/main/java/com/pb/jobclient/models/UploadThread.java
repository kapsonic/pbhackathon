package com.pb.jobclient.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UploadThread {
    public enum Status {
        REGISTERED, FINISHED
    }
    String filename;
    String key;
    Status status = Status.REGISTERED;

    public UploadThread(String file, String key) {
        filename = file;
        this.key = key;
        status = Status.REGISTERED;
    }
}
