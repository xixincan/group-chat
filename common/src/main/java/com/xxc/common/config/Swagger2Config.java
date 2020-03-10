package com.xxc.common.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: xixincan
 * @Date: 2019-05-22
 * @Version 1.0
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Value("${swagger.enable}")
    private Boolean swaggerEnable;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(this.swaggerEnable)
                .apiInfo(this.buildApiInfo())
                .select()
                //扫描指定包中的swagger注解
//                .apis(RequestHandlerSelectors.basePackage("com.xxc.web"))
                //扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger2接口文档")
                .description("内部调试对接专用Swagger2接口文档")
                .version("1.0.0")
                .build();
    }


}
