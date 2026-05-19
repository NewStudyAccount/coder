package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OssFile {

    private Long id;
    private String originalName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String url;
    private Long uploadUserId;
    private String uploadUserName;
    private LocalDateTime createTime;
}
