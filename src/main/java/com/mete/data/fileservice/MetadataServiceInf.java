package com.mete.data.fileservice;

import java.util.Date;
import java.util.List;

public interface MetadataServiceInf {

	FileMetadata create(Metadata metadata);
	byte[] getFileMetaData(String fileId);
	List<FileMetadata> findFiles(String profileName, Date date);
}
