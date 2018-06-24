package com.pb.jobclient.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemErrorHandler {

	
	private static SystemErrorHandler instance = new SystemErrorHandler();
	
	private SystemErrorHandler() {
		
	}
	
	public static SystemErrorHandler instance() {
		return instance;
	}
	
	public void onSystemError(Throwable e, Object event ) {
		log.error("Received a system error notification for event '{}'. Will abort and exit JVM", ( event != null ? event : "unknown event"),  e);
		System.exit(1);
	}
}
