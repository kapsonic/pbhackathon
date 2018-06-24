/**
 * 
 */
package com.pb.jobclient.models;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author AT002YA
 *
 */
public class PrintStreamDetail {

    private String id;

    private String enterpriseId; // The Enterprise/Company the user/job belongs to
    private String companyId;
    private String accumulationProfileId;
    private String correlationId; // maps to the Event created when the print stream is released

    private String status;  // init, pending, complete, cancelled, restarted
    private Date releasedOn;
    
    Set<FileInfo> responseFiles = new TreeSet<FileInfo>(Comparator.comparing(FileInfo::getDateCompleted).reversed());
   
    public Set<FileInfo> getResponseFiles(){
        Set<FileInfo> set = new TreeSet<FileInfo>(Comparator.comparing(FileInfo::getDateCompleted).reversed());
        set.addAll(this.responseFiles);
        return set;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAccumulationProfileId() {
        return accumulationProfileId;
    }

    public void setAccumulationProfileId(String accumulationProfileId) {
        this.accumulationProfileId = accumulationProfileId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getReleasedOn() {
        return releasedOn;
    }

    public void setReleasedOn(Date releasedOn) {
        this.releasedOn = releasedOn;
    }

    public void setResponseFiles(Set<FileInfo> responseFiles) {
        this.responseFiles = responseFiles;
    }
}
