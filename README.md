# SpringBoot整合Shiro
---
title: Swagger2
tags: SpringBoot,Swagger
grammar_cjkRuby: true
---

#### 1. 新建SpringBoot-Web项目
#### 2. 添加依赖
springfox-swagger2       
springfox-swagger-ui     
#### 3. 编写hello工程
#### 4. 配置swagger
SwaggerConfig.java
@Configuration
@EnableSwagger2
#### 5. 测试运行
/swagger-ui.html
四部分：组、swagger信息、接口信息、实体类信息。
#### 6. 配置swagger信息
##### 6.1 swagger的bean实例Docket  
```
package com.chenzj36.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.ArrayList;
/**
 * @Author Danny Lyons
 * @Email chenzj36@live.cn
 * @Time 2020/2/8 11:38
 * @Description Swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2 )
                .apiInfo(apiInfo());
    }

    //配置swagger信息apiInfo
    private ApiInfo apiInfo(){
        // 作者信息
        Contact contact = new Contact("Danny Lyons", "https://github.com/chenzj36", "chenzj36@live.cn");
        return new ApiInfo( //对应/swagger-ui.html首页的显示
                "chenzj36 Documentation",
                "即使再小的饭也能远航",
                "1.0",
                "urn:tos",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}
```
- 效果    
![enter description here](https://aliyunosschenzj.oss-cn-beijing.aliyuncs.com/aliyunoss/1581156660557.png)    
##### 6.2 配置扫描接口
- Docket.select()
```
 @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // RequestHandlerSelectors配置要扫描接口的方式
                // basePackage指定要扫描的包
                // any：扫描全部
                // none：都不扫描
                // withClassAnnotation：扫描类上的注解，参数是一个注解的反射对象
                // withMethodAnnotation：扫描方法上的注解 s.withMethodAnnotation(RestController.class)
                .apis(RequestHandlerSelectors.basePackage("com.chenzj36.controller"))
                // path：过滤哪些路径
                .paths(PathSelectors.ant("/chenzj36/**"))
                .build();
    }
```
##### 6.3 配置是否启动swagger
```
return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                // 选择是否启用swagger
                .enable(false)
```
- 题目：要求仅在生产环境启用swagger     
 ![enter description here](https://aliyunosschenzj.oss-cn-beijing.aliyuncs.com/aliyunoss/1581168294356.png)
#### 7. 配置API分组 
##### 7.1 配置API文档分组
` return new Docket(DocumentationType.SWAGGER_2).groupName("A");`
##### 7.3 配置多个分组
```
 @Bean
    public Docket docket1(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("A");
    }
    @Bean
    public Docket docket2(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("B");
    }
    @Bean
    public Docket docket3(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("C");
    }
```
##### 7.4 实体类配置
> *controller返回值中存在实体类，就会被扫描到*
> 给生成的文档加注释
>> @ApiModule()-->实体类
>> @ApiModuleProperty()-->字段 
>> @Api-->Module
>> @ApiOperation -->controller方法
>> @ApiParam-->controller方法参数的注释
- controller
```
@RequestMapping("/hello")
    @ResponseBody
    @ApiOperation("测试controller")
    public String hello(){
        return "hello";
    }

    @ApiOperation("只要返回值中有pojo，该实体类就会被swagger扫描到")
    @GetMapping("/helloUser")
    @ResponseBody
    public User helloUser(){
        return new User();
    }

    @ApiOperation("测试500以及post")
    @PostMapping("/pott")
    public String postt(@ApiParam("com.chenzj36.pojo下的User对象") User user){
        int i = 3/0;
        return "no return";
    }
```
- User.java
```
package com.chenzj36.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("User实体类")
public class User {
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
}

```
#### 8. Swagger执行测试      
***总结***  
- 我们可以通过Swagger给一些比较难理解的属性或者接口增加注释信息；
- 接口文档实时更新；
- 可以在线测试
- 注意：在正式发布的时候，一定要关闭Swagger。此举是出于安全考虑，此外也是节省运行内存的做法。
