package com.pb.jobclient.utils;


import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.SystemUtils.*;

@Slf4j
public class UserEnvironmentUtils {
	
	public static void logUserEnvironementDetails(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n************  OS Details  ************* ");
		sb.append("\r\n OS architecture : "+OS_ARCH);
		sb.append("\r\n OS Name "+OS_NAME);
		sb.append("\r\n OS Version "+OS_VERSION);
		sb.append("\r\n Path Sperator "+PATH_SEPARATOR);
		sb.append("\r\n USER_COUNTRY : "+USER_COUNTRY);
		sb.append("\r\n User Directory : "+USER_DIR);
		sb.append("\r\n User Home : " + USER_HOME);
		sb.append("\r\n User Language : "+USER_LANGUAGE);
		sb.append("\r\n User Name : "+USER_NAME);
		sb.append("\r\n User TimeZone : "+USER_TIMEZONE);
		
			
		log.info(sb.toString());
	}
	
	public static void logJavaDetails(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n ******* JAVA INFO ***********");
		sb.append("\r\n Java Runtime Name : "+JAVA_RUNTIME_NAME);
		sb.append("\r\n Java Runtime Version : "+JAVA_RUNTIME_VERSION);
		sb.append("\r\n Java Class Path : "+JAVA_CLASS_PATH);
		sb.append("\r\n Java Class Version : "+JAVA_CLASS_VERSION);		
		sb.append("\r\n Java Home : "+JAVA_HOME);
		sb.append("\r\n Java IO Temp Directory : "+JAVA_IO_TMPDIR);
		sb.append("\r\n Java Specification Vendor : "+JAVA_SPECIFICATION_VENDOR);		
		sb.append("\r\n Java Version : "+JAVA_VERSION);		
		sb.append("\r\n Java VM Name : "+JAVA_VM_NAME);
		
		log.info(sb.toString());
	}
	
	public static void logDesktopClientDetails(String relayClientVersion, String relayClientBuildDate, String configInstallationFolder){
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n ******* Desktop Client Info ***********");
        sb.append("\r\n Desktop Client Version : "+relayClientVersion);
        sb.append("\r\n Desktop Client Build Time : "+relayClientBuildDate);
        sb.append("\r\n Desktop Client Config Folder Path : "+configInstallationFolder);
        log.info(sb.toString());
    }
	
	
	public static void main(String[] args) {		
		logUserEnvironementDetails();
		logJavaDetails();
	}
	
	
}
