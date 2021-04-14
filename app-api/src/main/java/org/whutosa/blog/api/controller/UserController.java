package org.whutosa.blog.api.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.whutosa.blog.api.vo.UserVO;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.Constant;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.config.redis.RedisUtil;
import org.whutosa.blog.data.bo.valid.UserEditValidGroup;
import org.whutosa.blog.data.bo.valid.UserLoginValidGroup;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;
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

    /**
     * 管理员获取用户列表
     * @return 用户列表
     */
    @GetMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ApplicationResponse<List<UserVO>> getAll() {
        return ApplicationResponse.succeed(userService.findAll().stream()
                .map(UserVO::fromUser)
                .collect(Collectors.toList()));
    }

    /**
     * 获取登录信息
     * @return 登录信息
     */
    @GetMapping("/info")
    @RequiresAuthentication
    public ApplicationResponse<UserVO> info() {
        String account = JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT);
        User user = userService.getUserByAccount(account);
        // 用户是否存在
        if (user == null) {
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN,"未登录");
        }
        // 获取当前登录用户
        return ApplicationResponse.succeed("您已经登录了", UserVO.fromUser(user));
    }

    /**
     * 用户注册
     * @return 注册后信息
     */
    @PostMapping("/register")
    public ApplicationResponse<UserVO> register(@Validated(UserEditValidGroup.class) User user) {
        return add(user);
    }

    /**
     * 管理员新增用户
     * @return 新增后信息
     */
    @PostMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:add"})
    public ApplicationResponse<UserVO> add(@Validated(UserEditValidGroup.class) User user) {
        user.setDateRegister(new Timestamp(System.currentTimeMillis()));
        try {
            user = userService.addUser(user);
        } catch (Exception exception) {
            throw new ApplicationException(SystemCodeEnum.ARGUMENT_WRONG, exception.getMessage());
        }
        return ApplicationResponse.succeed(UserVO.fromUser(user));
    }

    /**
     * 用户登录
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApplicationResponse<Void> login(@Validated(UserLoginValidGroup.class) User user, HttpServletResponse httpServletResponse) throws Exception {
        if(!userService.match(user)){
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN, "帐号不存在或密码错误");
        }

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        RedisUtil.setObject(Constant.REDIS_REFRESH_TOKEN + user.getAccount(), currentTimeMillis,
                Long.parseLong(Constant.REFRESH_TOKEN_EXPIRE_TIME));
        String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
        httpServletResponse.setHeader("Authorization", token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        return ApplicationResponse.succeed("登录成功");
    }

    /**
     * 剔除登录状态
     * @return 剔除结果
     */
    @DeleteMapping("/online")
    public ApplicationResponse<String> deleteOnline(Integer id) {
        User user = userService.getOneOr(id, null);
        if(user==null){
            return ApplicationResponse.fail(SystemCodeEnum.ARGUMENT_WRONG, "用户不存在！");
        }
        String key = Constant.REDIS_REFRESH_TOKEN + user.getAccount();
        if(RedisUtil.hasKey(key)){
            RedisUtil.deleteKey(key);
        }
        return ApplicationResponse.succeed();
    }
}
