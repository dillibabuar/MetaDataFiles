package com.mete.data.fileservice;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

public class FileMetadata implements Serializable {
    
    static final long serialVersionUID = 7283287076019483950L;
    private static final Logger LOG = Logger.getLogger(FileMetadata.class);
    
    public static final String PROP_FILEID = "fileid";
    public static final String PROP_FILE_NAME = "file-name";
    public static final String PROP_FILE_DATE = "file-date";
    public static final String PROP_PROFILE_NAME = "profile-name";
    
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    
    

	protected String fileId;
    protected String fileName;
    protected Date fileDate;
    protected String profileName;
    
    
    public FileMetadata() {
        super();
    }

    public FileMetadata(String fileName, Date fileDate,String profileName) {
        this(UUID.randomUUID().toString(), fileName, fileDate,profileName);
    }
    
    public FileMetadata(String fileId, String fileName, Date fileDate,String profileName) {
        super();
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileDate = fileDate;
        this.profileName = profileName;
        
    }
    
    public FileMetadata(Properties properties) {
        this(properties.getProperty(PROP_FILEID),
             properties.getProperty(PROP_FILE_NAME),
             null,properties.getProperty(PROP_PROFILE_NAME));
        String dateString = properties.getProperty(PROP_FILE_DATE);
        if(dateString!=null) {
            try {
                this.fileDate = DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                LOG.error("Error while parsing date string: " + dateString + ", format is: yyyy-MM-dd" , e);
            }
        }    
    }

    public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getFileDate() {
		return fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    
    public Properties createProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_FILEID, getFileId());
        props.setProperty(PROP_FILE_NAME, getFileName());
        props.setProperty(PROP_FILE_DATE, DATE_FORMAT.format(getFileDate()));
        props.setProperty(PROP_PROFILE_NAME, getProfileName());
        return props;
    }
    
    
    

}
