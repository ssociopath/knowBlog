package org.whutosa.blog.data.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author bobo
 * @date 2021/4/20
 */

@Data
@Entity
@NoArgsConstructor
public class PunchIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer year;
    private Integer month;
    private Integer dailyBitmap;

    public PunchIn(Integer userId, Integer year, Integer month, Integer dailyBitmap) {
        this.userId = userId;
        this.year = year;
        this.month = month;
        this.dailyBitmap = dailyBitmap;
    }
}
