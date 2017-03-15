package com.mete.data.rest;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import com.mete.data.fileservice.FileMetadata;
import com.mete.data.fileservice.Metadata;
import com.mete.data.fileservice.MetadataServiceInf;


@Controller
@RequestMapping(value = "/restfile")
public class RestFileController {
	
	
private static final Logger LOG = Logger.getLogger(RestFileController.class);
    
    @Autowired
    MetadataServiceInf metadataService;
    
    public MetadataServiceInf getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(MetadataServiceInf metadataService) {
        this.metadataService = metadataService;
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody FileMetadata handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile file ,
            @RequestParam(value="profile", required=true) String profile,
            @RequestParam(value="date", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        
        try {
            Metadata metadata = new Metadata(file.getBytes(), file.getOriginalFilename(), date, profile );
            getMetadataService().create(metadata);
            return metadata.getMetadata();
        } catch (RuntimeException e) {
            LOG.error("Error while uploading.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Error while uploading.", e);
            throw new RuntimeException(e);
        }      
    }
    
    
    
    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public HttpEntity<List<FileMetadata>> findDocument(
            @RequestParam(value="profile", required=false) String profile,
            @RequestParam(value="date", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<List<FileMetadata>>(getMetadataService().findFiles(profile,date), httpHeaders,HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getDocument(@PathVariable String id) {         
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(getMetadataService().getFileMetaData(id), httpHeaders, HttpStatus.OK);
    }

}
