package com.mete.data.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mete.data.fileservice.FileMetadata;
import com.mete.data.fileservice.Metadata;

@Service("MetadataDao")
public class FileSystemMetadataDao implements FileMetaDataDaoInf {
	
	private static final Logger LOG = Logger.getLogger(FileSystemMetadataDao.class);
    
    public static final String DIRECTORY = "test-images";
    public static final String META_DATA_FILE_NAME = "metadata.properties";
    
    @PostConstruct
    public void init() {
        createDirectory(DIRECTORY);
    }

	@Override
	public void insert(Metadata metadata) {
		try {
            createDirectory(metadata);
            saveFileData(metadata);
            saveMetaData(metadata);
        } catch (IOException e) {
            String message = "Error while inserting document";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }

	}

	@Override
	public Metadata load(String fileId) {
		try {
            return loadFromFileSystem(fileId);
        } catch (IOException e) {
            String message = "Error while loading document with id: " + fileId;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
	}
	
	
	private FileMetadata loadMetadataFromFileSystem(String uuid) throws IOException {
		FileMetadata metadata = null;
        String dirPath = getDirectoryPath(uuid);
        File file = new File(dirPath);
        if(file.exists()) {
            Properties properties = readProperties(uuid);
            metadata = new FileMetadata(properties);
            
        } 
        return metadata;
    }
	
	@Override
    public List<FileMetadata> findByProfileNameDate(String profileName, Date date) {
        try {
            return findInFileSystem(profileName,date);
        } catch (IOException e) {
            String message = "Error while finding document, profile name: " + profileName + ", date:" + date;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
	
	
	private List<FileMetadata> findInFileSystem(String profileName, Date date) throws IOException  {
        List<String> uuidList = getUuidList();
        List<FileMetadata> metadataList = new ArrayList<FileMetadata>(uuidList.size());
        for (String uuid : uuidList) {
        	FileMetadata metadata = loadMetadataFromFileSystem(uuid);         
            if(isMatched(metadata, profileName, date)) {
                metadataList.add(metadata);
            }
        }
        return metadataList;
    }
	
	private boolean isMatched(FileMetadata metadata, String profileName, Date date) {
        if(metadata==null) {
            return false;
        }
        boolean match = true;
        if(profileName!=null) {
            match = (profileName.equals(metadata.getProfileName()));
        }
        if(match && date!=null) {
            match = (date.equals(metadata.getFileDate()));
        }
        return match;
    }
	
	private Metadata loadFromFileSystem(String uuid) throws IOException {
	       FileMetadata metadata = loadMetadataFromFileSystem(uuid);
	       if(metadata==null) {
	           return null;
	       }
	       Path path = Paths.get(getFilePath(metadata));
	       Metadata document = new Metadata(metadata);
	       document.setFileData(Files.readAllBytes(path));
	       return document;
	    }

	    private String getFilePath(FileMetadata metadata) {
	        String dirPath = getDirectoryPath(metadata.getFileId());
	        StringBuilder sb = new StringBuilder();
	        sb.append(dirPath).append(File.separator).append(metadata.getFileName());
	        return sb.toString();
	    }
	    
	    private void saveFileData(Metadata metadata) throws IOException {
	        String path = getDirectoryPath(metadata);
	        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(new File(path), metadata.getFileName())));
	        stream.write(metadata.getFileData());
	        stream.close();
	    }
	    
	    public void saveMetaData(Metadata metadata) throws IOException {
	            String path = getDirectoryPath(metadata);
	            Properties props = metadata.createProperties();
	            File f = new File(new File(path), META_DATA_FILE_NAME);
	            OutputStream out = new FileOutputStream( f );
	            props.store(out, "Document meta data");       
	    }
	    
	    private List<String> getUuidList() {
	        File file = new File(DIRECTORY);
	        String[] directories = file.list(new FilenameFilter() {
	          @Override
	          public boolean accept(File current, String name) {
	            return new File(current, name).isDirectory();
	          }
	        });
	        return Arrays.asList(directories);
	    }
	    
	    private Properties readProperties(String uuid) throws IOException {
	        Properties prop = new Properties();
	        InputStream input = null;     
	        try {
	            input = new FileInputStream(new File(getDirectoryPath(uuid),META_DATA_FILE_NAME));
	            prop.load(input);
	        } finally {
	            if (input != null) {
	                try {
	                    input.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return prop;
	    }
	    
	    private String createDirectory(Metadata metadata) {
	        String path = getDirectoryPath(metadata);
	        createDirectory(path);
	        return path;
	    }

	    private String getDirectoryPath(Metadata metadata) {
	       return getDirectoryPath(metadata.getFileId());
	    }
	    
	    private String getDirectoryPath(String uuid) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(DIRECTORY).append(File.separator).append(uuid);
	        String path = sb.toString();
	        return path;
	    }

	    private void createDirectory(String path) {
	        File file = new File(path);
	        file.mkdirs();
	    }

}
