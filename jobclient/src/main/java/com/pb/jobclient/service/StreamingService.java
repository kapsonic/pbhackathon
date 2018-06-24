package com.pb.jobclient.service;

import com.google.common.io.ByteStreams;
import com.pb.jobclient.models.ActiveUpload;
import com.pb.jobclient.models.Company;
import com.pb.jobclient.models.Upload;
import lombok.extern.slf4j.Slf4j;
import com.pb.jobclient.models.UploadChunk;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.*;

@Slf4j
@Service
public class StreamingService {

    private RelayClient relayClient;

    public void streamUpload(Upload upload, File file, Company.Workflow workflow) {

        try {
            waitForFile(file);
        } catch (FileNotFoundException e) {
            return;
        }

        if (upload.getTotalSize() == 0) {
            upload.setTotalSize(file.length());
        }

        int retryCount = 3;

        long chunkSize = relayClient.getChunkSize();
        int maxOffset = (int) Math.max(Math.ceil(((double) upload.getTotalSize()) / ((double) chunkSize)), 1d);
        boolean success = false;
        boolean ignored = false;
        for (int offset = 0; offset < maxOffset; offset++) {
            long startByte = offset * chunkSize;
            long endByte = Math.min(upload.getTotalSize(), (offset + 1) * chunkSize);

            long uploadChunkSize = endByte - startByte;
            int chunkNumber = offset + 1;

            UploadChunk chunk = new UploadChunk(upload, chunkNumber, uploadChunkSize);
            boolean hasChunk = false;
            try {
                hasChunk = relayClient.hasChunk(chunk);
            } catch (HttpServerErrorException e) {
                log.error("Error occured while checking for chunk", e);
                return;
            }
            if (!hasChunk) {
                File f = new File(upload.getPath());
                if (!f.canRead()) {
                    success = false;
                    break;
                }

                try (FileInputStream in = new FileInputStream(f)) {
                    ByteStreams.skipFully(in, startByte);
                    //InputStream chunkedInputStream = ByteStreams.limit(in, endByte);
                    InputStream chunkedInputStream = ByteStreams.limit(in, uploadChunkSize);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(chunkedInputStream, baos);

                    byte[] chunkedBytes = baos.toByteArray();

                    // ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
                    // chunk.setIn(bais);

                    //InputStreamResource r = new InputStreamResource(chunkedInputStream);
                    // chunk.setResource(r);

                    // chunk.setIn(bais);
                    chunk.setchunkedBytes(chunkedBytes);
                    int numRetries = retryCount;
                    do {
                        log.info("Trying to upload chunk, Retry left: [{}]", numRetries);
                        try {
                            log.info("Uploading chunk {} {} bytes( Calling Rest API )  for file {} and workflow ", chunkNumber, uploadChunkSize, file.getAbsolutePath(), workflow.getName());
                            if (numRetries < retryCount) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            success = relayClient.uploadChunk(chunk);

                        } catch (HttpClientErrorException | HttpServerErrorException e) {
                            log.error("Error occured while uploading chunk", e);
                            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode()) && f.exists()) {
                                // probably due to upload identifier is deleted by backend job
                                // and now we have to create upload request to process this.
                                try {
                                    in.close();
                                } catch (Exception e1) {
                                    log.error("Exception closing stream", e1);
                                }

                                success = false;
                                ignored = true;
                                break;
                            }
                        } catch (RestClientException e) {
                            log.error("Rest Client Error while uploading file: " + file, e);
                            success = false;
                        }
                    } while (!success && (--numRetries) > 0);


                    if (!success)
                        break;
                } catch (FileNotFoundException e) {
                    log.error("Could not find file {} for upload", file);
                    success = false;
                } catch (IOException e) {
                    log.error("Error while uploading file: " + file, e);
                    success = false;
                }
            }
        }
        if (ignored) {
            return;
        }
        if (success) {
            log.info("Successfully Uploaded all the chunks for file {} and workflow ", file.getAbsolutePath(), workflow.getName());

        } else {
            log.info("Failure Uploaded all the chunks for file {} and workflow ", file.getAbsolutePath(), workflow.getName());

        }
    }

    @Autowired
    public StreamingService(RelayClient relayClient) {
        this.relayClient = relayClient;
    }

    /**
     * Waits until the file has been completely written to the hot folder.
     *
     * @param file The file to wait for
     */
    private void waitForFile(File file) throws FileNotFoundException {
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
    }

    /**
     * Checks if an write operation is in progress for the specified <code>file</code> by attempting
     * to get a FileInputStream for the file.
     *
     * @param file The file to check
     * @return true if the file has been completely written
     */
    private boolean isCompletelyWritten(File file) throws FileNotFoundException {

        try {
            FileInputStream in = new FileInputStream(file);
            log.info("Was able to open FileInputStream! Assuming file has been completely written to folder.");
            in.close();
            return true;
        } catch (FileNotFoundException e) {
            // Expect this exception if the FileInputStream cannot be opened.
            //log.info("isCompletelyWrittenFIS: File Not Found Exception: {}", e.toString());
            throw e;
        } catch (IOException e) {
            log.error("File IO exception: {}", e.toString());
        }
        return false;
    }

}
