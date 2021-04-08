package org.whutosa.blog.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author bobo
 * @date 2021/4/5
 */

@Data
@Entity
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String name;
    private String code;
}
