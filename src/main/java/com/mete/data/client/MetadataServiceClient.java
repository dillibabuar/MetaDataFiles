package com.mete.data.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mete.data.fileservice.FileMetadata;
import com.mete.data.fileservice.Metadata;
import com.mete.data.fileservice.MetadataServiceInf;

public class MetadataServiceClient implements MetadataServiceInf {

	
	private static final Logger LOG = Logger.getLogger(MetadataServiceClient.class);
    
    String protocol = "http";
    String hostname = "localhost";
    Integer port = 8080;
    String baseUrl = "archive";
    
    RestTemplate restTemplate;
    
	@Override
	public FileMetadata create(Metadata metadata) {
		try {          
            return doSave(metadata);
        } catch (RuntimeException e) {
            LOG.error("Error while uploading file", e);
            throw e;
        } catch (IOException e) {
            LOG.error("Error while uploading file", e);
            throw new RuntimeException("Error while uploading file", e);
        }
	}
	
	
	private FileMetadata doSave(Metadata metadata) throws IOException, FileNotFoundException {
        String tempFilePath = writeDocumentToTempFile(metadata);
        LOG.info("===========tempFilePath==============================="+tempFilePath);
        MultiValueMap<String, Object> parts = createMultipartFileParam(tempFilePath);
        String dateString = FileMetadata.DATE_FORMAT.format(metadata.getFileDate());
        FileMetadata documentMetadata = getRestTemplate().postForObject(getServiceUrl() + "/upload?profile={name}&date={date}", 
                parts, 
                FileMetadata.class,
                metadata.getProfileName(), 
                dateString);
        return documentMetadata;
    }

	@Override
	public byte[] getFileMetaData(String fileId) {
		return getRestTemplate().getForObject(getServiceUrl() +  "/document/{id}", byte[].class, fileId);
	}

	@Override
	public List<FileMetadata> findFiles(String profileName, Date date) {
		String dateString = null;
        if(date!=null) {           
            dateString = FileMetadata.DATE_FORMAT.format(date);
        }
        FileMetadata[] result = getRestTemplate().getForObject(getServiceUrl() +  "documents?profile={name}&date={date}", FileMetadata[].class, profileName, dateString);
        LOG.info("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+result.length);
        return Arrays.asList(result);
	}
	
	
	private MultiValueMap<String, Object> createMultipartFileParam(String tempFilePath) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();           
        parts.add("file", new FileSystemResource(tempFilePath));
        return parts;
    }

    private String writeDocumentToTempFile(Metadata metadata) throws IOException, FileNotFoundException {
        Path path;       
        path = Files.createTempDirectory(metadata.getFileId());  
        String tempDirPath = path.toString();
        File file = new File("c:\\test-images\\",metadata.getFileName());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(metadata.getFileData());    
        fo.close();
        return file.getPath();
    }
    
    public String getServiceUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProtocol()).append("://");
        sb.append(getHostname());
        if(getPort()!=null) {
            sb.append(":").append(getPort());
        }
        sb.append("/").append(getBaseUrl()).append("/");
        return sb.toString();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RestTemplate getRestTemplate() {
        if(restTemplate==null) {
            restTemplate = createRestTemplate(); 
        }
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate createRestTemplate() {
        restTemplate = new RestTemplate();
        return restTemplate;
    }


}
