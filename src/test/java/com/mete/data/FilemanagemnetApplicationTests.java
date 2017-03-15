package com.mete.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.context.ApplicationContext;

import com.mete.data.client.MetadataServiceClient;
import com.mete.data.dao.FileSystemMetadataDao;
import com.mete.data.fileservice.FileMetadata;
import com.mete.data.fileservice.Metadata;
import com.mete.data.fileservice.MetadataServiceInf;


@RunWith(SpringRunner.class)
@SpringBootTest
@SpringApplicationConfiguration
@WebAppConfiguration
@IntegrationTest
public class FilemanagemnetApplicationTests {

	
private static final Logger LOG = Logger.getLogger(FilemanagemnetApplicationTests.class);
    
    private static final String TEST_FILE_DIR = "test-images";

    ApplicationContext applicationContext;
    
    MetadataServiceInf client;
    
    
    @Before
    public void setUp() throws IOException {
        client = new MetadataServiceClient();
        testUpload();
    }

    @After
    public void tearDown() {
    //	testFindDocuments();
      //  deleteDirectory(new File(FileSystemMetadataDao.DIRECTORY));
    }
	
    
    @Test
    public void testFindDocuments() {
        List<FileMetadata> result = client.findFiles(getProfileName(), null);
        assertNotNull("Result is null", result);
        assertTrue("Result is empty", !result.isEmpty());
        for (FileMetadata fileMetadata : result) {
            assertEquals("Person name is not : " + getProfileName(), getProfileName(), fileMetadata.getProfileName());
        }
    }

    @Test
    public void testUpload() throws IOException {
        List<String> fileList = getFileList();
        LOG.info("==============================="+fileList.size());
        for (String fileName : fileList) {
            uploadFile(fileName);
        }
        testFindDocuments();
    }

    private void uploadFile(String fileName) throws IOException {
    	try{
    	LOG.info("==============================="+fileName);
        StringBuilder sb = new StringBuilder();
        sb.append(TEST_FILE_DIR).append(File.separator).append(fileName);
        LOG.info("================dir==============="+sb.toString());
        Path path = Paths.get(sb.toString());
        byte[] fileData = Files.readAllBytes(path);
        Date today = Calendar.getInstance().getTime();
        String profileName = getProfileName();  
        LOG.info("================profileName==============="+profileName);
        FileMetadata metadata = client.create(new Metadata(fileData, fileName, today, profileName));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Document saved, uuid: " + metadata.getFileId());
        }
        
    	} catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
          }
    }

    private String getProfileName() {
        return this.getClass().getSimpleName();
    }

    private List<String> getFileList() {
        File file = new File(TEST_FILE_DIR);
        String[] files = file.list(new FilenameFilter() {
			
		    @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile();
            }
        });
        return Arrays.asList(files);
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
    
   
}
