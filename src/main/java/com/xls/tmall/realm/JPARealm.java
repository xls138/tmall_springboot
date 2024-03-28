
package com.xls.tmall.realm;

import com.xls.tmall.pojo.User;
import com.xls.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

//定义了一个公开的类JPARealm，它继承自Shiro的AuthorizingRealm类
public class JPARealm extends AuthorizingRealm {

    //这里使用了Spring的自动装配（Autowired）功能来注入UserService，这是一个服务类，用于获取用户信息
    @Autowired
    private UserService userService;

    //这是授权方法的重写。当需要获取用户的角色和权限信息时，Shiro会调用此方法。
    //在这个示例中，该方法简单地返回了一个新的SimpleAuthorizationInfo实例，没有进行任何角色或权限的设置
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    //这是认证方法的重写。当需要验证用户身份时，Shiro会调用此方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //从AuthenticationToken中获取用户名
        String userName = token.getPrincipal().toString();
        //使用userService根据用户名获取用户实体User
        User user = userService.getByName(userName);
        //从用户实体中获取存储在数据库中的密码和盐值
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        //创建一个 SimpleAuthenticationInfo 对象，其中包含了用户名、数据库中的密码和盐值。这个对象将被 Shiro 用于后续的密码比对验证。
        //ByteSource.Util.bytes(salt) 是将盐值转换为 Shiro 能理解的格式。
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt),
                getName());
        return authenticationInfo;
    }

}

