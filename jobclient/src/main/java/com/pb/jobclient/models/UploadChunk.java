package com.pb.jobclient.models;

import lombok.Data;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@Data
public class UploadChunk {

    private int number;
    private String identifier;
    private long totalSize;
    private long size;
    private Resource resource;
    private byte[] chunkedBytes;
    private Upload upload;
    private String filename;
    private Resource contentsAsResource;
    
    public UploadChunk() {
    }

    public UploadChunk(Upload upload, int number, long size) {
        this.number = number;
        this.size = size;

        totalSize = upload.getTotalSize();
        identifier = upload.getIdentifier();
        filename = upload.getFilename();
        
    }
    
    public void setchunkedBytes(byte[] chunkedBytes) {
    	this.chunkedBytes = chunkedBytes;
    }
    
    public Resource getResource() {
    	if ( contentsAsResource != null ) return contentsAsResource;
    	if ( chunkedBytes == null )
    		throw new IllegalStateException("Invalid Resource. Byte array content is null" );
    	
    	contentsAsResource = createResource();
    	return contentsAsResource;
    }
    
    private Resource createResource () {
    	ByteArrayResource contentsAsResource = new ByteArrayResource(this.chunkedBytes){
            @Override
            public String getFilename(){
                return filename;
            }
        };
        return contentsAsResource;
    }
}
