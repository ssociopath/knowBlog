package org.whutosa.blog.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

/**
 * @author bobo
 * @date 2021/4/20
 */
@Data
@Entity
@NoArgsConstructor
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String content;
    private Date date;
    private Integer status;

    public Todo(Integer id) {
        this.id=id;
    }

    public Todo(Integer userId, String content, Date date, Integer status) {
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.status = status;
    }
}
