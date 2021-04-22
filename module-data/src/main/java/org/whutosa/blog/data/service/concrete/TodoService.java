package org.whutosa.blog.data.service.concrete;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.data.dto.BatchOperationResultDTO;
import org.whutosa.blog.data.dto.OperationResultDTO;
import org.whutosa.blog.data.entity.Todo;
import org.whutosa.blog.data.repository.concrete.TodoRepository;
import org.whutosa.blog.data.service.BaseDataService;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bobo
 * @date 2021/4/20
 */

@Slf4j
@Service
public class TodoService extends BaseDataService<Todo, Integer> {
    @Resource
    private TodoRepository todoRepository;

    public Todo updateTodo(Integer id, String content, Integer userId){
        Date date = new Date(System.currentTimeMillis());
        Todo todo = null==id? new Todo(userId, content, date, 0) :
                this.getOneOrThrow(id, ()->new ApplicationException(SystemCodeEnum.ARGUMENT_WRONG, "不存在该todo"));
        todo.setContent(content);
        return this.save(todo);
    }

    public Todo checkTodoList(Integer id){
        Todo todo = getOneOrThrow(id, ()->new ApplicationException(SystemCodeEnum.ARGUMENT_WRONG, "不存在该todo"));
        todo.setStatus(todo.getStatus()==0?1:0);
        this.save(todo);
        return todo;
    }

    public List<Todo> findAllByDate(long time, Integer userId){
        return todoRepository.findAllByUserIdAndDate(userId, new Date(time));
    }

}
