package com.pb.jobclient.utils;

public class Constants {
	
    public static final String LAST_SYNC_TIMESTAMP = "_lst";
    public static final String DEFAULT_PORT_SCAN_RANGE = "20000-25000";
    
    //public static final int downloadPollingTimeInSeconds = 5;
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    public static final String 	UPLOAD_FOLDER_NAME = "upload";
    public static final String 	DOWNLOAD_FOLDER_NAME = "download";
    public static final String 	USER_HOME_RELAY = ".relay";
    public static final String 	USER_RELAY_CREDENTIALS = "rdc.ini";
    
    
    public static final String COMPLETED_FOLDER_NAME = "_completed";
    public static final String PROCESSING_FOLDER_NAME = "_processing";
    public static final String FAILED_FOLDER_NAME = "_failed";
    public static final String REJECTED_FOLDER_NAME = "_rejected";

    
    public static final String RELAY_US_REGION = "Relay US Region";
    public static final String RELAY_EU_REGION = "Relay EU Region";
    public static final String RELAY_CA_REGION = "Relay CA Region";
    public static final String DEFAULT_RELAY_REGION = RELAY_US_REGION;

    public static final String USERNAME = "rdc1val";
    public static final String PASSWORD = "rdc2val";
    public static final String SERVER_URL = "serverUrl";
    public static final String LOCAL_HOT_FOLDER = "localHotFolder";
    public static final String PORT_SCAN = "portScan";
    public static final String SERVICE_NAME = "serviceName";
    public static final String SELECT = "Select";
    public static final String AUTO_START = "autoStart";
    public static final String AUTO_UPDATE = "autoUpdate";
    
    public static final int LOGIN_FAILURE_EXIT_CODE_1 = 1;
    public static final int CONFIG_FAILURE_EXIT_CODE_2 = 2;
    public static final int WATCH_FAILURE_EXIT_CODE_3 = 3;
	public static final int NO_WATCH_EXIT_CODE_4 = 4;
	public static final int PERMISSION_ISSUE_EXIT_CODE_5 = 5;
	public static final int MULTIPLE_INSTANCES_EXIT_CODE_6 = 6;
	public static final String UPDATE_MESSAGE = "An update is available";

    public static final String STATUS_QUERY = "status";
    public static final String BACKUP_FOLDER = "backup";
    public static final String UPDATER_TMP_FOLDER = "updater-tmp";
    public static final String DEFAULT_SERVICE_NAME="RelayDesktopClientService";
    public static final String RELAY_UPDATER_PREFIX = "relay-client-updater";
    public static final String SERVICE_STATUS_PROPERTY = "serviceStatus";

    public static final String DESKTOP_MODE = "desktop";
    public static final String SERVICE_MODE = "service";
    public static final String INSTALLED_SERVICE_NAME = "PBRCHDesktopClientService";

    public static final String SERVICE_INSTALL_PATH = "serviceInstallPath";

}
