package com.pb.jobclient.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

@Data
public class Download {

	public enum DownloadStatus {
		IN_PROGRESS, FAILED, COMPLETED;
	}

	private String eventId;

	private String s3BucketFilename;

	@JsonIgnore
	private Path holderDownloadPath;
	
	@JsonIgnore
	private Path errorPath;

	private byte[] content;
	
	private File downloadedFile;

	private DownloadStatus status;
	
	private Company company;
	
	private Date date;
	
	private String printStreamDetailId;
	
	private Date releasedOn;
	
	private Date createdOn;
	
	private String customerFileName;

	private String workflowName;
	
	/**
	 * This hash is used to detect if the file has changed locally since an
	 * upload operation began. It is calculated based on the files path, length
	 * and last modified date and does NOT include the content of the file.
	 */
	@JsonIgnore
	private String hash;

	public Download() {
		status = Download.DownloadStatus.IN_PROGRESS;
	}
	
	protected void setFailed() {
		status = DownloadStatus.FAILED;
	}
	
	protected void setCompleted() {
		status = DownloadStatus.COMPLETED;
	}

	public boolean isSuccess() {
		return status == DownloadStatus.COMPLETED;
	}

	public void setDownloadFile(File file) {
		this.downloadedFile = file;
		if ( file == null )
			setFailed();
		else 
			setCompleted();
	}
	
	public void setContent( byte[] content ) {
		this.content =  content;
		if ( content == null )
			setFailed();
		else 
			setCompleted();
	}
	
	
}
