package org.whutosa.blog.api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.data.dto.valid.UserEditValidGroup;
import org.whutosa.blog.data.dto.valid.UserLoginValidGroup;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
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
    public ApplicationResponse<String> login(@Validated(UserLoginValidGroup.class) User user) {
        if(!userService.match(user)){
            return ApplicationResponse.fail(SystemCodeEnum.NEED_LOGIN, "帐号不存在或密码错误！");
        }
        return ApplicationResponse.succeed();
    }
}
