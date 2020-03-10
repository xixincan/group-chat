package com.xxc.web.config;

import com.xxc.web.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * 默认首页
 *
 * @author xixincan
 * 2020-03-09
 * @version 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:index.html");
    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations. Interceptors can be registered to apply
     * to all requests or be limited to a subset of URL patterns.
     * <p><strong>Note</strong> that interceptors registered here only apply to
     * controllers and not to resource handler requests. To intercept requests for
     * static resources either declare a
     * {@link MappedInterceptor MappedInterceptor}
     * bean or switch to advanced configuration mode by extending
     * {@link WebMvcConfigurationSupport
     * WebMvcConfigurationSupport} and then override {@code resourceHandlerMapping}.
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.accessLimitInterceptor).addPathPatterns("/**");
    }
}