package com.xxc.service;

import com.xxc.entity.response.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
public interface IFileUploadService {

    FileInfo upload(MultipartFile file, HttpServletRequest request);
}
