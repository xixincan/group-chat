package com.xxc;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 当打包方式为war时用到
 * 另外需要在POM中过滤掉tomcat插件
 *
 * @Author: xixincan
 * @Date: 2019-05-22
 * @Version 1.0
 */
public class ApplicationForWar extends SpringBootServletInitializer {

    /**
     * Configure the application. Normally all you would need to do is to add sources
     * (e.g. config classes) because other settings have sensible defaults. You might
     * choose (for instance) to add default command line arguments, or set an active
     * Spring profile.
     *
     * @param builder a builder for the application context
     * @return the application builder
     * @see SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //指向Jar方式打包的启动类
        return builder.sources(Application.class);
    }
}
