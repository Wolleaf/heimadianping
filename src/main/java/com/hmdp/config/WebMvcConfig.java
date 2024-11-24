package com.hmdp.config;

import com.hmdp.interceptor.LoginInterceptor;
import com.hmdp.interceptor.RefreshTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private final LoginInterceptor loginInterceptor;
    private final RefreshTokenInterceptor refreshTokenInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册拦截器...");
        registry.addInterceptor(refreshTokenInterceptor).order(0);
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/blog/hot",
                        "/shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/voucher/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources",
                        "/error",
                        "/v2/**"
                ).order(1);
    }

    /**
     * 通过knife4j生成用户端接口文档
     * @return
     */
    @Bean
    public Docket userDocket() {
        log.info("开始创建管理端接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("黑马点评项目接口文档")
                .version("2.0")
                .description("黑马点评项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hmdp.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
