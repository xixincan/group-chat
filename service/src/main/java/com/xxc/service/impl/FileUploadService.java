package com.xxc.service.impl;

import cn.hutool.log.StaticLog;
import com.xxc.common.consts.ConfigKey;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyFileUtil;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.response.FileInfo;
import com.xxc.service.IConfigService;
import com.xxc.service.IFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Value("${server.port}")
    private int port;

    @Resource
    private IConfigService configService;

    @Override
    public FileInfo upload(MultipartFile file, HttpServletRequest request) {
        // 重命名文件，防止重名
        String filename = EncryptUtil.genRandomID();
        String originalFilename = file.getOriginalFilename();
        String fileSize = MyFileUtil.getFormatSize(file.getSize());
        String suffix = "";
        // 截取文件的后缀名
        if (null != originalFilename && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        filename = filename + suffix;
        String path = this.configService.getValue(ConfigKey.FILE_STORE_DIR);
        String prefix = request.getSession().getServletContext().getRealPath(path);

        StaticLog.info("存储路径为:{}/{}", prefix, filename);
        Path filePath = Paths.get(prefix, filename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new AccessException("文件上传发生错误:" + e.getMessage());
        }
        //TODO file url
        String address = MyIPUtil.getIP() + this.port;
        //todo 再参考修改 https://blog.csdn.net/qq_32662595/article/details/90519752
        String fileUrl = "http://" + address + "/ROOT/" + filename;
        return new FileInfo().setOriginalFilename(originalFilename).setFileSize(fileSize).setFileUrl(fileUrl);
    }

}
