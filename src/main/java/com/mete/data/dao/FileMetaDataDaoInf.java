package com.mete.data.dao;

import java.util.Date;
import java.util.List;

import com.mete.data.fileservice.FileMetadata;
import com.mete.data.fileservice.Metadata;

public interface FileMetaDataDaoInf {

	void insert(Metadata metadata);
	Metadata load(String fileId);
	List<FileMetadata> findByProfileNameDate(String profileName, Date date);
}
