package com.dumas.pedestal.framework.swagger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger配置
 *
 * @author dumas
 * @date 2021/12/06 2:45 PM
 */
@Configuration
@EnableSwagger2WebMvc
@ConditionalOnProperty(name = "doc.enabled", prefix = "swagger", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {
    /**
     * 创建一个Docket对象
     * 调用select()方法，
     * 生成ApiSelectorBuilder对象实例，该对象负责定义外漏的API入口
     * 通过使用RequestHandlerSelectors和PathSelectors来提供Predicate，在此我们使用any()方法，将所有API都通过Swagger进行文档管理
     */
    @Value("${swagger.doc.title:标题}")
    private String docTitle;
    @Value("${swagger.doc.desc:接口描述}")
    private String docDesc;
    @Value("${swagger.doc.enabled:true}")
    private Boolean enabled;

    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("x-token").description("令牌").modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo()).select()
                // 按包分组
                .apis(RequestHandlerSelectors.basePackage("com.dumas"))
                .paths(PathSelectors.any())
                .build();
                //.pathMapping("/api");
                //.globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //标题
                .title(docTitle)
                //简介
                .description(docDesc)
                //服务条款
                .termsOfServiceUrl("服务条款信息:")
                //作者个人信息
                //.contact(new Contact("dumas", "", "1352841962@qq.com"))
                //版本
                .version("1.0").build();
    }

    /**
     * 设置请求头
     *
     * @return
     */
    private List<Parameter> setHeaderToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("X-Auth-Token").description("token").modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }
}
