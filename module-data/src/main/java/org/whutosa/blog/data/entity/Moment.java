package org.whutosa.blog.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author bobo
 * @date 2021/4/10
 */

@Data
@Entity
@NoArgsConstructor
public class Moment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer userId;
    private String mdContent;
    private String urlList;
    private Timestamp publishDate;
    private Integer status;

    public Moment(Integer id, String title, Integer userId, String mdContent, String urlList, String publishDate, Integer status) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.mdContent = mdContent;
        this.urlList = urlList;
        this.status = status;
        try {
            this.publishDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(publishDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPublishDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(publishDate);
    }
}
