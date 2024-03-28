
package com.xls.tmall.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xls.tmall.realm.JPARealm;

//标记这个类是一个配置类，Spring容器会将其作为配置信息处理。
@Configuration
public class ShiroConfiguration {
    //这个Bean是Shiro框架中的一个管理器，用于在Spring中管理Shiro Bean的生命周期，确保实现了Shiro框架生命周期接口的Bean可以正常工作。
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    //定义了一个Shiro过滤器工厂Bean，它是Shiro集成到Web应用中的关键组件。
    //此方法接受一个SecurityManager类型的参数，并将其设置到ShiroFilterFactoryBean中，用于创建Shiro过滤器，以实现安全控制。
    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        return shiroFilterFactoryBean;
    }

    //配置了Shiro的核心组件SecurityManager，这里使用的是DefaultWebSecurityManager。
    //此方法设置了自定义的Realm（通过getJPARealm()方法获取），这个Realm用于处理应用中的认证和授权操作。
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getJPARealm());
        return securityManager;
    }

    //定义了自定义的Realm - JPARealm，这是连接Shiro与应用安全数据（如用户信息、角色、权限等）的桥梁。
    //在这个Realm中设置了一个凭证匹配器，用于加密密码比对。
    @Bean
    public JPARealm getJPARealm() {
        JPARealm myShiroRealm = new JPARealm();
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    //配置了密码加密匹配器，这里使用了MD5算法，并设置了散列迭代次数为2。
    //这意味着系统存储的密码将是用户密码MD5加密2次的结果。这个加密匹配器会被设置到自定义的JPARealm中，用于密码的校验过程。
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }


    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    //开启了Shiro的AOP注解支持。通过这个Bean，可以在代码中使用如@RequiresRoles、@RequiresPermissions等注解，来实现方法级别的安全控制。
    //这个方法接收SecurityManager作为参数，并将其设置到AuthorizationAttributeSourceAdvisor中。
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}

