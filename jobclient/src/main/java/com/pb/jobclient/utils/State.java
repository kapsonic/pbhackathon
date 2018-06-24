package com.pb.jobclient.utils;

public class State {
	private static int uploadCount = 0;
	private static int downloadCount = 0;
	
	public static int getUploadCounter() {
		return uploadCount;
	}
	
	public static int getDownloadCounter() {
		return downloadCount;
	}
	
	public synchronized static void incrementUploadCounter() {
		uploadCount++;
	}
	
	public synchronized static void decrementUploadCounter() {
		uploadCount--;
	}
	
	public synchronized static void incrementDownloadCounter() {
		downloadCount++;
	}
	
	public synchronized static void decrementDownloadCounter() {
		downloadCount--;
	}
	
	public static boolean isApplicationIdle() {
		return uploadCount == 0 && downloadCount == 0;
	}
}
