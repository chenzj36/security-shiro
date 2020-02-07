package com.chenzj36.config;

import com.chenzj36.pojo.User;
import com.chenzj36.service.UserServiceImpl;
import jdk.nashorn.internal.parser.Token;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserServiceImpl userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权==>doGetAuthorizationInfo");

        //给用户授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermission("user:add");
        //拿到user对象
        Subject subject = SecurityUtils.getSubject();
        //从数据库拿到该用户的权限并加到该用户中
        User currentUser = (User)subject.getPrincipal();
        info.addStringPermission(currentUser.getPerms());
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证......");
        UsernamePasswordToken userToken = (UsernamePasswordToken)authenticationToken;
        //连接真实的数据库
        User user = userService.queryUserByName(userToken.getUsername());
        System.out.println("执行认证==>"+userToken.getUsername());
        if (user==null){ //没有此用户
            return null;
        }


        Subject currentSubject = SecurityUtils.getSubject();
        currentSubject.getSession().setAttribute("user",user);

        //盐值
        ByteSource byteSource = ByteSource.Util.bytes(user.getUsername());
        //第一个参数传user，授权的时候使用
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), "");
        info.setCredentialsSalt(byteSource);
        return info;
    }
}
