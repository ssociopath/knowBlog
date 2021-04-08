package org.whutosa.blog.api.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.SystemCodeEnum;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author bobo
 * @date 2021/4/7
 */

@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 查看当前Header中是否携带Authorization属性(Token)，有的话就进行登录认证授权
        if (this.isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                // 认证出现异常，传递错误信息msg
                String msg = e.getMessage();
                log.info(msg);
//                // 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
//                Throwable throwable = e.getCause();
//                if (throwable instanceof SignatureVerificationException) {
//                    // 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
//                    msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
//                } else if (throwable instanceof TokenExpiredException) {
//                    // 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
//                    if (this.refreshToken(request, response)) {
//                        return true;
//                    } else {
//                        msg = "Token已过期(" + throwable.getMessage() + ")";
//                    }
//                } else {
//                    // 应用异常不为空
//                    if (throwable != null) {
//                        // 获取应用异常msg
//                        msg = throwable.getMessage();
//                    }
//                }
//                // Token认证失败直接返回Response信息
//                this.response401(response, msg);
                return false;
            }
        } else {
            throw new ApplicationException(SystemCodeEnum.NEED_LOGIN);
        }
        return true;
    }

    /**
     * 这里我们详细说明下为什么重写
     * 可以对比父类方法，只是将executeLogin方法调用去除了
     * 如果没有去除将会循环调用doGetAuthenticationInfo方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        String token = getAuthzHeader(request);
        return token != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        JwtToken token = new JwtToken(getAuthzHeader(request));
        // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        return true;
    }
}
