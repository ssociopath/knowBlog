package org.whutosa.blog.api.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.dto.valid.UserEditValidGroup;
import org.whutosa.blog.data.dto.valid.UserLoginValidGroup;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;


/**
 * @author bobo
 * @date 2021/3/31
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping
    public ApplicationResponse<List<User>> getAll() {
        return ApplicationResponse.succeed(userService.findAll());
    }

    @PostMapping
    public ApplicationResponse<String> add(@Validated(UserEditValidGroup.class) User user) {
        user.setDateRegister(new Timestamp(System.currentTimeMillis()));
        userService.addUser(user);
        return ApplicationResponse.succeed();
    }

    @PostMapping("/login")
    public ApplicationResponse<String> login(@Validated(UserLoginValidGroup.class) User user, HttpServletResponse httpServletResponse) {
        if(!userService.match(user)){
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN, "帐号不存在或密码错误！");
        }
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
        httpServletResponse.setHeader("Authorization", token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        return ApplicationResponse.succeed("登录成功");
    }
}
