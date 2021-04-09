package org.whutosa.blog.api.config.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.Constant;
import org.whutosa.blog.common.utils.misc.JwtUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author bobo
 * @date 2021/4/7
 */

@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private StringRedisTemplate redisTemplate;

    public JwtFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
                    if(refreshToken(request, response)){
                        return true;
                    } else{
                        msg = "Token已过期(" + throwable.getMessage() + ")";
                    }
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

    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        String token = getAuthzHeader(request);
        // 获取当前Token的帐号信息
        String account = JwtUtil.getClaim(token, "account");
        // 判断Redis中RefreshToken是否存在
        if (Boolean.TRUE.equals(redisTemplate.hasKey(Constant.REDIS_REFRESH_TOKEN + account))) {
            // Redis中RefreshToken还存在，获取RefreshToken的时间戳
            String currentTimeMillisRedis = Objects.requireNonNull(redisTemplate.opsForValue()
                    .get(Constant.REDIS_REFRESH_TOKEN + account)).toString();
            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            if (JwtUtil.getClaim(token, JwtUtil.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                // 获取当前最新时间戳
                String currentTimeMillis = String.valueOf(System.currentTimeMillis());
                // 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
                redisTemplate.opsForValue().set(Constant.REDIS_REFRESH_TOKEN + account, currentTimeMillis,
                        Long.parseLong(Constant.REFRESH_TOKEN_EXPIRE_TIME), TimeUnit.SECONDS);
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = JwtUtil.sign(account, currentTimeMillis);
                // 将新刷新的AccessToken再次进行Shiro的登录
                JwtToken jwtToken = new JwtToken(token);
                // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
                this.getSubject(request, response).login(jwtToken);
                // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                httpServletResponse.setHeader("Authorization", token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                return true;
            }
        }
        return false;
    }

}
