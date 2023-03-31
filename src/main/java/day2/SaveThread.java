package day2;

import java.security.*;
import java.security.acl.Permission;

/**
 * 提供线程给第三方使用，需要确保线程的安全
 */
public abstract class SaveThread implements Runnable{
    //第三方实现的方法
    public abstract void  protectMethod();
    @Override
    public void run() {

        //设置安全管理器
        SecurityManager securityManager = new SecurityManager();
        System.setSecurityManager(securityManager);

        //代码来源
        CodeSource codeSource = new CodeSource(null, (CodeSigner[]) null);
        //权限
        Permissions permission = new Permissions();
        //保护域
        ProtectionDomain protectionDomain = new ProtectionDomain(codeSource, permission);
        AccessControlContext accessControlContext = new AccessControlContext(new ProtectionDomain[]{protectionDomain});

        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                protectMethod();
                return null;
            }
        },accessControlContext);

    }
}
