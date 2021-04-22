package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.Todo;
import org.whutosa.blog.data.repository.DataRepository;

import java.sql.Date;
import java.util.List;

/**
 * @author bobo
 * @date 2021/4/20
 */

public interface TodoRepository extends DataRepository<Todo, Integer> {
    List<Todo> findAllByUserIdAndDate(Integer userId, Date date);
}
