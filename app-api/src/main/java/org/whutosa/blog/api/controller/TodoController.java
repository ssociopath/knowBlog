package org.whutosa.blog.api.controller;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.dto.BatchOperationResultDTO;
import org.whutosa.blog.data.entity.Todo;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.TodoService;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bobo
 * @date 2021/4/21
 */

@RestController
@RequestMapping("/todo")
public class TodoController {
    @Resource
    TodoService todoService;
    @Resource
    UserService userService;

    @GetMapping
    @RequiresAuthentication
    public ApplicationResponse<List<Todo>> getTodoList(long time){
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        return ApplicationResponse.succeed(todoService.findAllByDate(time, user.getId()));
    }

    @PostMapping
    @RequiresAuthentication
    public ApplicationResponse<Todo> updateTodo(Integer id, String content){
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        return ApplicationResponse.succeed(todoService.updateTodo(id, content, user.getId()));
    }

    @PostMapping("/check")
    @RequiresAuthentication
    public ApplicationResponse<Todo> checkTodo(Integer id){
        return ApplicationResponse.succeed(todoService.checkTodoList(id));
    }

    @DeleteMapping
    public ApplicationResponse<Void> deleteTodo(Integer id){
        todoService.delete(new Todo(id));
        return ApplicationResponse.succeed();
    }
}
