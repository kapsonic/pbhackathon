package com.pb.jobclient.service;

import com.pb.jobclient.models.ActiveUpload;
import com.pb.jobclient.models.Company.Workflow;
import com.pb.jobclient.models.Upload;
import com.pb.jobclient.models.Upload.UploadStatus;
import com.pb.jobclient.models.UploadThread;
import com.pb.jobclient.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class UploadService {

    private RelayClient relayClient;
    private StreamingService streamingService;
    private ApplicationEventPublisher publisher;

    private TaskExecutor createUploadExecutor;

    @Autowired
    public UploadService(
            RelayClient relayClient,
            StreamingService streamingService
    ) {
        this.relayClient = relayClient;
        this.streamingService = streamingService;
    }


    public Upload startUpload(File file, Workflow workflow) {
        return startUpload(file, workflow, false);
    }

    private Upload startUpload(File file, Workflow workflow, boolean isBundleFile) {
        Path path = waitForFile(file);

        if (path != null) {
            file = path.toFile();
        }

        try {
            String key = "";

            if (workflow.isBundleWorkflow() && !isBundleFile) {
                key = "bundle/" + file.getName();
            } else {
                key = workflow.getKey(file.getName());
            }
            Upload upload = new Upload(file, key);
            // Override JobName in case of bundle workflows to show workflow name
            if (workflow.isBundleWorkflow() && isBundleFile) {
                upload.setJobName(workflow.getName());
            }
            //setting job profile is
            upload.setJobProfileId(workflow.getJobProfileId());
            // initiate upload on server
            try {
                Upload result = relayClient.initiateUpload(upload);
                upload.setIdentifier(result.getIdentifier());

                log.info("Starting upload. Will first insert DB. file {}, workflow {} and identifier {}", file.getAbsolutePath(), workflow.getName(), result.getIdentifier());
                return upload;
            } catch (RestClientException e) {
                log.error("Error uploading file {}", file, e);
                throw e;
            }

        } catch (RestClientException e) {
            log.error("Error uploading file {}", file, e);
//            cancelUpload(new ActiveUpload(null, file, workflow), true);
            throw e;
        }
    }


    private Path waitForFile(File file) {
        final long sleepTimeMs = 5000;
        final long waitLimitMs = 30 * 60 * 1000;    // 30 minutes. It would have to be huge!
        long elapsedTimeMs = 0;
        while (!isCompletelyWritten(file) && elapsedTimeMs < waitLimitMs) {
            try {
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                log.error("While waiting for file {} to be written: {}", e.getMessage());
            }
            elapsedTimeMs = elapsedTimeMs + sleepTimeMs;

            // Log every 10 seconds
            if (elapsedTimeMs % (2 * sleepTimeMs) == 0)
                log.info("Waiting {} SECONDS more for file to be completely written to folder before giving up: {}", ((waitLimitMs - elapsedTimeMs) / 1000L), file.getAbsolutePath());
        }
        Path path = null;

        return path;
    }

    /**
     * Checks if an write operation is in progress for the specified <code>file</code> by attempting
     * to get a FileInputStream for the file.
     *
     * @param file The file to check
     * @return true if the file has been completely written
     */
    private boolean isCompletelyWritten(File file) {

        try (FileInputStream in = new FileInputStream(file)) {
            log.info("Was able to open FileInputStream! Assuming file has been completely written to folder.");
            return true;
        } catch (FileNotFoundException e) {
            // Expect this exception if the FileInputStream cannot be opened.
            //log.info("isCompletelyWrittenFIS: File Not Found Exception: {}", e.toString());
        } catch (IOException e) {
            log.error("File IO exception: {}", e.toString());
        }
        return false;
    }
}
