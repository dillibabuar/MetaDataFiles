package com.mete.data.fileservice;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mete.data.dao.FileMetaDataDaoInf;


@Service("metadataService")
public class MetadataService implements MetadataServiceInf , Serializable {
	
    private static final long serialVersionUID = 8119784722798361327L;
    
    @Autowired
    private FileMetaDataDaoInf fileMetaDataDao;
    
    public FileMetaDataDaoInf getFileMetaDataDao() {
        return fileMetaDataDao;
    }

    public void setDocumentDao(FileMetaDataDaoInf fileMetaDataDao) {
    	this.fileMetaDataDao = fileMetaDataDao;
    }

	@Override
	public FileMetadata create(Metadata metadata) {
		getFileMetaDataDao().insert(metadata); 
        return metadata.getMetadata();
	}

	@Override
	public byte[] getFileMetaData(String fileId) {
		Metadata metadata = getFileMetaDataDao().load(fileId);
        if(metadata!=null) {
            return metadata.getFileData();
        } else {
            return null;
        }
	}
	
	@Override
    public List<FileMetadata> findFiles(String profileName, Date date) {
        return getFileMetaDataDao().findByProfileNameDate(profileName, date);
    }

}
