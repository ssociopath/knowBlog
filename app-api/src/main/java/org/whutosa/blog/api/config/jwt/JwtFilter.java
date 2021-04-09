package org.whutosa.blog.api.config.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
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
                SystemCodeEnum code = SystemCodeEnum.NEED_LOGIN;;
                Throwable throwable = e.getCause();
                String msg = e.getMessage();
                // 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
                if (throwable instanceof SignatureVerificationException) {
                    // 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
                    msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
                } else if (throwable instanceof TokenExpiredException) {
                    msg = "Token已过期(" + throwable.getMessage() + ")";
                } else if (throwable != null) {
                    // 获取应用异常msg
                    msg = throwable.getMessage();
                    code = SystemCodeEnum.SERVER_INNER_ERROR;
                }
                sendError(request, response, msg, code.toString());
                return false;
            }
        } else {
            sendError(request, response, "当前请求Authorization属性(Token)为空",
                    SystemCodeEnum.NEED_LOGIN.toString());
            return false;
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

    @SneakyThrows
    private void sendError(ServletRequest request, ServletResponse response, String msg, String code){
        log.info(msg);
        request.setAttribute("filter.error.msg", msg);
        request.setAttribute("filter.error.code", code);
        request.getRequestDispatcher("/error/throw").forward(request, response);
    }


}
