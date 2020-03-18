package com.xxc.web.config;

import com.xxc.service.IFileUploadService;
import com.xxc.web.interceptor.AccessLimitInterceptor;
import com.xxc.web.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;

/**
 * 默认首页
 *
 * @author xixincan
 * 2020-03-09
 * @version 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${chat.file.path.mapping}")
    private String fileMappingPath;

    @Value("${chat.file.upload.macos}")
    private String macOSFilePath;

    @Value("${chat.file.upload.windows}")
    private String windowsFilePath;

    @Value("${chat.file.upload.linux}")
    private String linuxFilePath;

    @Resource
    private AccessLimitInterceptor accessLimitInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private IFileUploadService fileUploadService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.accessLimitInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(this.loginInterceptor)
                .excludePathPatterns("/error", "/static/**", "/favico.ico")
                .addPathPatterns("/**");
    }

    /**
     * Configure cross origin requests processing.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods("*").maxAge(1800);
    }

    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //判断当前的系统
        String pathPattern = this.fileMappingPath + "**";
        ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(pathPattern);
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            //Windows
            // fileMappingPath 表示在磁盘windowsFilePath目录下的所有资源会被解析为以下的路径
            resourceHandlerRegistration.addResourceLocations("file:" + this.windowsFilePath);
            this.fileUploadService.initUploadFileDir(this.windowsFilePath);
        } else if (os.startsWith("mac")) {
            //macOS
            resourceHandlerRegistration.addResourceLocations("file:" + this.macOSFilePath);
            this.fileUploadService.initUploadFileDir(this.macOSFilePath);
        } else {
            //Linux
            resourceHandlerRegistration.addResourceLocations("file:" + this.linuxFilePath);
            this.fileUploadService.initUploadFileDir(this.linuxFilePath);
        }
    }
}