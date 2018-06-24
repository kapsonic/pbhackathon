/**
 * 
 */
package com.pb.jobclient.models;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @author AT002YA
 *
 */
@Data
public class Job {
    
    private String id;

    private String jobName;
    
    private Date updatedOn;
    
    private String printStreamDetailId;
    
    Set<FileInfo> responseFiles ;
    
    private String workflowKey;
    
    private String workflowName;
    
}
