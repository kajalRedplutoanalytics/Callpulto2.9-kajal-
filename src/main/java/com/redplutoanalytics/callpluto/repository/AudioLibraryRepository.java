package com.redplutoanalytics.callpluto.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redplutoanalytics.callpluto.model.AudioFiles;

@Repository
public interface AudioLibraryRepository {
	  List<Map<String, Object>> findFilteredAudioFiles(Map<String, Object> filters);
	  
	  void insertTradingOrder(Map<String, Object> row);
	  void insertRecordingDetails(Map<String, Object> row);

	  
	  
}