package com.redplutoanalytics.callpluto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processing_jobs")
public class ProcessingJobs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "recording_details_file")
    private String recordingDetailsFile;

    @Column(name = "audio_files_count")
    private Integer audioFilesCount;

    @Column(name = "status")
    private String status;

    @Column(name = "match_count")
    private Integer matchCount;

    @Column(name = "mismatch_count")
    private Integer mismatchCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getRecordingDetailsFile() {
		return recordingDetailsFile;
	}

	public void setRecordingDetailsFile(String recordingDetailsFile) {
		this.recordingDetailsFile = recordingDetailsFile;
	}

	public Integer getAudioFilesCount() {
		return audioFilesCount;
	}

	public void setAudioFilesCount(Integer audioFilesCount) {
		this.audioFilesCount = audioFilesCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(Integer matchCount) {
		this.matchCount = matchCount;
	}

	public Integer getMismatchCount() {
		return mismatchCount;
	}

	public void setMismatchCount(Integer mismatchCount) {
		this.mismatchCount = mismatchCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

 
}
