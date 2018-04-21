package com.bigdata.sampler.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;


public class ShiroTutorial {


    public static void main(String[] args) {

        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager=factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject currentUser=SecurityUtils.getSubject();

        Session session = currentUser.getSession();

        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");

        System.out.println(session.getAttribute("someKey"));

        if(!currentUser.isAuthenticated()){
            //UsernamePasswordToken token=new UsernamePasswordToken("lonestarr","vespa");
            UsernamePasswordToken token=new UsernamePasswordToken("hailong","123456");

            token.setRememberMe(true);

            currentUser.login(token);
        }


        System.out.println(currentUser.isPermitted("user1:update"));


        System.out.println(currentUser.isPermitted("user1:view"));

        currentUser.logout();


    }
}
