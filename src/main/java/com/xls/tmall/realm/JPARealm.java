
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

public class JPARealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = token.getPrincipal().toString();
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        //创建一个 SimpleAuthenticationInfo 对象，其中包含了用户名、数据库中的密码和盐值。这个对象将被 Shiro 用于后续的密码比对验证。
        //ByteSource.Util.bytes(salt) 是将盐值转换为 Shiro 能理解的格式。
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt),
                getName());
        return authenticationInfo;
    }

}

