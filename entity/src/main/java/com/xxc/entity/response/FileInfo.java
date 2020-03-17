package com.xxc.entity.response;

import java.io.Serializable;

/**
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
public class FileInfo implements Serializable {

    private String originalFilename;

    private String fileSize;

    private String fileUrl;

    public String getOriginalFilename() {
        return originalFilename;
    }

    public FileInfo setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public String getFileSize() {
        return fileSize;
    }

    public FileInfo setFileSize(String fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public FileInfo setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }
}
