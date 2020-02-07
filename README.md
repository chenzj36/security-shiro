# SpringBoot整合Shiro

### 快速开始     
#### 介绍   
- 官网：http://shiro.apache.org/     
- Github：https://github.com/apache/shiro     
#### Quick Start     
1.IDEA中新建maven项目     
2.新建一个Module     
3.为pom文件添加依赖     
```
   <!-- configure logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>jcl-over-slf4j</artifactId>
            <version>2.0.0-alpha1</version>

        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>2.0.0-alpha1</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
    </dependencies>
```
4. 配置文件（resources）      
- log4j.properties
- shiro.ini
5. HelloShiro.java     
6. 运行程序     
7. 打印日志信息         
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1580965045021.png)
8. 读源码找关键
```
 // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();
		
// Do some stuff with a Session (no need for a web or EJB container!!!)
        Session session = currentUser.getSession();
		
// 判断当前用户是否被认证
// Do some stuff with a Session (no need for a web or EJB container!!!)
        Session session = currentUser.getSession();
//获得当前用户的认证
		currentUser.getPrincipal()
//获得当前用户的角色
		currentUser.hasRole("schwartz")
//当前用户获得的权限
		currentUser.isPermitted("lightsaber:wield")
//all done - log out!
        currentUser.logout();
```
#### SpringBoot整合Shiro
1. **新建SpringBoot Module**     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1580976172651.png)    
2. **添加shiro-spring依赖**     
<!-- https://mvnrepository.com/artifact/org.apache.shiro/shiro-spring -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.5.0</version>
        </dependency>
3. **编写config**
- UserRealm.java
```
package com.chenzj36.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
public class UserRealm extends AuthorizingRealm {
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权==>doGetAuthorizationInfo");
        return null;
    }
    
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
```
- ShiroConfig.java
```
package com.chenzj36.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
public class ShiroConfig {
   //ShiroFilterFactoryBean s-3
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(securityManager);

        //添加Shiro内置过滤器
        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/user/add","authc");
//        filterMap.put("/user/update","authc");
        filterMap.put("/user/*","authc");
        bean.setFilterChainDefinitionMap(filterMap);

        //没有权限转到登录页
        bean.setLoginUrl("/toLogin");

        return bean;
    }

    //DefaultWebSecurityManager s-2
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(userRealm);
        return defaultWebSecurityManager;
    }

    //创建realm对象
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }
}
```
4. **编写测试的页面**
- index.html
```
<body>
<h1>hello</h1>
<a th:text="${msg}"></a>
<hr>
<a th:href="@{/user/add}">add</a> | <a th:href="@{/user/update}">update</a>
</body>
```
- user/add.html
```
<body>
<h1>add</h1>
</body>
```
- user/update.html
```
<body>
<h1>update</h1>
</body>
```
- MyController.java
```
package com.chenzj36.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class MyController {
    @GetMapping({"/","/index","/index.html"})
    public String index(Model model){
        model.addAttribute("msg","hello,Shiro.");
        return "index";
    }

    @RequestMapping("/user/add")
    public String add(){
        return "user/add";
    }

    @RequestMapping("/user/update")
    public String update(){
        return "user/update";
    }
}
```
5. **Shiro实现登录拦截**     
- Shiro的内置过滤器     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1580980458038.png)    

- getShiroFilterFactoryBean方法中添加代码
```
//添加Shiro内置过滤器
        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/user/add","authc");
//        filterMap.put("/user/update","authc");
        filterMap.put("/user/*","authc");
        bean.setFilterChainDefinitionMap(filterMap);
		 //没有权限转到登录页
        bean.setLoginUrl("/toLogin"); //跳转到login.html
```
- login.html
```
<body>
<h1>登录</h1>
<hr>
<p th:text="${msg}" style="color: red;"></p>
<form th:action="@{/login}">
    <p>用户名：<input type="text" name="username"></p>
    <p>密码：<input type="text" name="password"></p>
    <p><input type="submit"></p>
</form>
</body>
```
- controller
```
 @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
```
6. **用户认证**
- Controller
```
@RequestMapping("/login")
    public String login(String username, String password, Model model){
        //获取当前用户
        Subject subject = SecurityUtils.getSubject();
        //封装用户的登录信息
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token); //执行登录的方法
            System.out.println("认证成功");
            return "index";
        }catch (UnknownAccountException e){
            model.addAttribute("msg","用户名错误");
            return  "login";
        }catch (IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误");
            return "login";
        }
    }
```
- UserRealm认证方法
```
//认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("执行认证......");
        String username = "root";
        String password = "123456";
        UsernamePasswordToken userToken = (UsernamePasswordToken)authenticationToken;
        System.out.println("执行认证==>"+userToken.getUsername());
        if (!userToken.getUsername().equals(username)){
            return null;
        }

        return new SimpleAuthenticationInfo("", password,"");
    }
```
7. **整合MyBatis**
7.1 添加依赖
```
  <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.21</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.1</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
            <scope>provided</scope>
        </dependency>
```
7.2 配置yml，properties
```spring:
  datasource:
    username: root
    password: chen
    url: jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource


    #Spring Boot 默认是不注入这些属性值的，需要自己绑定
    #druid 数据源专有配置
    initialSize: 5
    minIdle: 5
    maxActive: 20
    maxWait: 60000
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true

    #配置监控统计拦截的filters，stat:监控统计、log4j：日志记录、wall：防御sql注入
    #如果允许时报错  java.lang.ClassNotFoundException: org.apache.log4j.Priority
    #则导入 log4j 依赖即可，Maven 地址： https://mvnrepository.com/artifact/log4j/log4j
    filters: stat,wall,log4j
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
```
```
#指定Mybatis的核心配置文件所在路径
mybatis.mapper-locations=classpath:mapper/*.xml
#对应实体类的路径(xml中并无关于实体类文件位置的配置，故需在此配置)
mybatis.type-aliases-package=com.chenzj36.pojo
```
7.3 pojo/User.java
```
package com.chenzj36.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String username;
    private String password;
    private String perms;
}
```
7.4 mapper/UserMapper.java
```
package com.chenzj36.mapper;

import com.chenzj36.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    public User queryUserByName(String name);
}
```
7.5 classpath/mapper/UserMapper.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--mapper namespace填写接口类的完整路径-->
<mapper namespace="com.chenzj36.mapper.UserMapper">

    <!--id对应接口类中的方法名，resultType对应返回值中封装的基本类型-->
    <select id="queryUserByName" resultType="User">
    select * from mybatis.user where username=#{name}
  </select>

</mapper>
```
7.6 service层
*UserService接口*
```
package com.chenzj36.service;

import com.chenzj36.pojo.User;

public interface UserService {
    public User queryUserByName(String name);

}
```
*UserServiceImpl.java*
```
package com.chenzj36.service;

import com.chenzj36.mapper.UserMapper;
import com.chenzj36.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User queryUserByName(String name) {
        return userMapper.queryUserByName(name);
    }
}
```
7.7 UserRealm中修改为从数据库中取用户信息         
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581044393356.png)         
7.8 注销         
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581045236450.png)     
7.9 密码加密MD5（盐值加密）在认证方法中
```
//盐值
ByteSource byteSource = ByteSource.Util.bytes(user.getUsername());
//第一个参数传user，授权的时候使用
SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,user.getPassword(), "");
info.setCredentialsSalt(byteSource);
return info;
```
8. **授权**
- 为页面添加权限要求     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581046891821.png)
- 设置未授权跳转页面getShiroFilterFactoryBean     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581046821455.png)     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581046780658.png)
- 授予用户权限（检查权限）     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581046664848.png)         
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581046699509.png)    
9. **整合thymeleaf**
- 整合依赖包
```
<!-- https://mvnrepository.com/artifact/com.github.theborakompanioni/thymeleaf-extras-shiro -->
<dependency>
	<groupId>com.github.theborakompanioni</groupId>
	<artifactId>thymeleaf-extras-shiro</artifactId>
	<version>2.0.0</version>
</dependency>
```
- 整合ShiroDialect,ShiroConfig.java     
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581047394772.png)
- 前端测试 xmlns:shiro="http://www.thymeleaf.org/thymeleaf-extras-shiro"
```
<body>
<h1>hello</h1>

<div th:if="${session.user==null}">
    <p>
        <a th:href="@{/toLogin}">登录</a>
    </p>
</div>

<hr>
<div shiro:hasPermission="user:add">
    <a th:href="@{/user/add}">add</a>
</div>

<div shiro:hasPermission="user:update">
    <a th:href="@{/user/update}">update</a>
</div>

</body>
```
- 认证之后加session         
![enter description here](http://q5053ip41.bkt.clouddn.com/xsj/1581047561163.png)
