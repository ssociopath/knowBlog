package org.whutosa.blog.api.module.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.whutosa.blog.data.entity.Moment;


/**
 * @author bobo
 * @date 2021/4/20
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentQuery {
    private Integer id;
    private String title;
    private String mdContent;
    private String htmlContent;
    private String urlList;
    private Integer status;
    private String[] tags;

    public Moment toMoment(){
        return new Moment(id, title, null, mdContent, urlList, null, status);
    }
}
