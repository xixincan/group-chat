package com.xxc.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyFileUtil;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.response.FileInfo;
import com.xxc.service.IFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
@Service
public class FileUploadService implements IFileUploadService {

    @Value("${chat.file.path.mapping}")
    private String fileMappingPath;
    private String uploadFileDir;

    @Override
    public void initUploadFileDir(String uploadFileDir) {
        this.uploadFileDir = uploadFileDir;
    }

    @Override
    public FileInfo upload(MultipartFile file, HttpServletRequest request) {
        // 重命名文件，防止重名
        String filename;
        try {
            filename = EncryptUtil.md5(file.getBytes());
        } catch (IOException e) {
            StaticLog.warn("file.getBytes() exp:{}", e);
            filename = EncryptUtil.genRandomID();
        }
        String originalFilename = file.getOriginalFilename();
        String fileSize = MyFileUtil.getFormatSize(file.getSize());
        String suffix = "";
        // 截取文件的后缀名
        if (null != originalFilename && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        filename = filename + suffix;
        StaticLog.info("存储路径为:{}{}", this.uploadFileDir, filename);

        //检测是否有相同文件，防止重复上传 - 没有用专门的静态文件存储服务器，解决不了相同文件传入不容服务器中的重复问题
        if (!this.checkDupFile(filename)) {
            Path filePath = Paths.get(this.uploadFileDir, filename);
            try {
                Files.copy(file.getInputStream(), filePath);
            } catch (IOException e) {
                throw new AccessException("文件上传发生错误:" + e.getMessage());
            }
        } else {
            StaticLog.warn("发现重复的文件内容，直接返回");
        }

        // address ==>> http : // host : port
//        String address = request.getRequestURL().toString().replace(request.getRequestURI(), "");
//        String fileUrl = address + this.fileMappingPath + filename;
        // 上述方法得出的地址没法解决集群部署的问题 -> 这里使用本机IP地址取代
        String http = request.getScheme();
        String ip = MyIPUtil.getIP();
        int port = request.getServerPort();
        String fileUrl = http + "://" + ip + ":" + port + this.fileMappingPath + filename;
        return new FileInfo().setOriginalFilename(originalFilename).setFileSize(fileSize).setFileUrl(fileUrl);
    }

    private boolean checkDupFile(String filename) {
        File dir = new File(this.uploadFileDir);
        File[] listFiles = dir.listFiles((parentDir, name) -> StrUtil.equals(filename, name));
        return null != listFiles && listFiles.length > 0;
    }

}
