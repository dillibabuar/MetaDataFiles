package com.mete.data.fileservice;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public class Metadata extends FileMetadata implements Serializable {

	
private static final long serialVersionUID = 2004955454853853315L;
    
    private byte[] fileData;
    
    public Metadata( byte[] fileData, String fileName, Date documentDate,String profileName) {
        super(fileName, documentDate,profileName);
        this.fileData = fileData;
    }

    public Metadata(Properties properties) {
        super(properties);
    }
    
    public Metadata(FileMetadata metadata) {
        super(metadata.getFileId(), metadata.getFileName(), metadata.getFileDate(), metadata.getProfileName());
    }

    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    public FileMetadata getMetadata() {
        return new FileMetadata(getFileId(), getFileName(), getFileDate(),getProfileName());
    }
    
}
