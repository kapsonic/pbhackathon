package com.pb.jobclient.models;

import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.List;

@ToString
public class Company {

    private List<Workflow> workflows;

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }

    public static class Workflow {
        private String name;
        private String s3Folder;
        private boolean bundleWorkflow = false;
        private String folderPath;
        private boolean accumulationWorkflow = false; // Flag to indicate to the system that this is an Accumulation WF
        private boolean presortWorkflow = false; // Flag to indicate to the system that is an presort WF.
        private String jobProfileId;
        private boolean isComplete;

        public Workflow() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getS3Folder() {
            return s3Folder;
        }

        public void setS3Folder(String s3Folder) {
            this.s3Folder = s3Folder;
        }

        public boolean isBundleWorkflow() {
            return bundleWorkflow;
        }

        public void setBundleWorkflow(boolean bundleWorkflow) {
            this.bundleWorkflow = bundleWorkflow;
        }
        
        public String getJobProfileId() {
            return jobProfileId;
        }

        public void setJobProfileId(String jobProfileId) {
            this.jobProfileId = jobProfileId;
        }
        
        public String getFolderPath() {
        	if ( folderPath == null )
        		throw new RuntimeException("Folder path not configured by user");
            return folderPath;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }

        public void setFolderPath(String folderPath) {
            this.folderPath = folderPath;
        }
        
        public boolean isAccumulationWorkflow() {
            return accumulationWorkflow;
        }
        
        public void setAccumulationWorkflow(boolean accumulationWorkflow) {
            this.accumulationWorkflow = accumulationWorkflow;
        }

        public boolean isPresortWorkflow() {
            return presortWorkflow;
        }
        
        public void sePresortWorkflow(boolean presortWorkflow) {
            this.presortWorkflow = presortWorkflow;
        }

        public String getKey(String filename) {
            String key = filename;

            if (key == null || StringUtils.isEmpty(s3Folder) || s3Folder.matches("[/s//]*"))
                return key;
            
            String s3Path = "";
            if(s3Folder.startsWith("sobUpload/")){
                s3Path = s3Folder;
            }else{
                s3Path = s3Folder.replaceAll("^[/s/]*(.*)[/s]*$", "$1");
            }
            if (!s3Path.endsWith("/"))
                s3Path += "/";

            return s3Path + key;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((s3Folder == null) ? 0 : s3Folder.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Workflow other = (Workflow) obj;
            if (s3Folder == null) {
                if (other.s3Folder != null)
                    return false;
            } else if (!s3Folder.equals(other.s3Folder))
                return false;
            return true;
        }
    }
}
