package org.whutosa.blog.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author bobo
 * @date 2021/4/14
 */

@Data
@Entity
@NoArgsConstructor
public class MomentTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer momentId;
    private Integer tagId;

    public MomentTag(Integer momentId, Integer tagId) {
        this.momentId = momentId;
        this.tagId = tagId;
    }
}
