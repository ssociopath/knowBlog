package org.whutosa.blog.api.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.whutosa.blog.api.vo.UserVO;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.Constant;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.dto.valid.UserEditValidGroup;
import org.whutosa.blog.data.dto.valid.UserLoginValidGroup;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author bobo
 * @date 2021/3/31
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @GetMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ApplicationResponse<List<UserVO>> getAll() {
        return ApplicationResponse.succeed(userService.findAll().stream()
                .map(UserVO::fromUser)
                .collect(Collectors.toList()));
    }

    @GetMapping("/info")
    @RequiresAuthentication
    public ApplicationResponse<UserVO> info() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        String account = JwtUtil.getClaim(token, JwtUtil.ACCOUNT);
        User user = userService.getUserByAccount(account);
        // 用户是否存在
        if (user == null) {
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN,"未登录");
        }
        // 获取当前登录用户
        return ApplicationResponse.succeed("您已经登录了", UserVO.fromUser(user));
    }

    @PostMapping
    public ApplicationResponse<UserVO> add(@Validated(UserEditValidGroup.class) User user) {
        user.setDateRegister(new Timestamp(System.currentTimeMillis()));
        return ApplicationResponse.succeed(UserVO.fromUser(user));
    }

    @PostMapping("/login")
    public ApplicationResponse<Void> login(@Validated(UserLoginValidGroup.class) User user, HttpServletResponse httpServletResponse) throws Exception {
        if(!userService.match(user)){
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN, "帐号不存在或密码错误");
        }

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        stringRedisTemplate.opsForValue().set(Constant.REDIS_REFRESH_TOKEN + user.getAccount(), currentTimeMillis,
                Long.parseLong(Constant.REFRESH_TOKEN_EXPIRE_TIME), TimeUnit.SECONDS);
        String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
        httpServletResponse.setHeader("Authorization", token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        return ApplicationResponse.succeed("登录成功");
    }

    @DeleteMapping("/online")
    public ApplicationResponse<String> deleteOnline(Integer id) {
        User user = userService.getOneOr(id, null);
        if(user==null){
            return ApplicationResponse.fail(SystemCodeEnum.ARGUMENT_WRONG, "用户不存在！");
        }
        String key = Constant.REDIS_REFRESH_TOKEN + user.getAccount();
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            stringRedisTemplate.delete(key);
        }
        return ApplicationResponse.succeed();
    }
}
