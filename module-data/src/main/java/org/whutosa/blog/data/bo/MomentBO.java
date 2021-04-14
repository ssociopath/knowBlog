package org.whutosa.blog.data.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.whutosa.blog.data.entity.Moment;
import org.whutosa.blog.data.entity.Tag;
import org.whutosa.blog.data.entity.User;

import java.util.List;

/**
 * @author bobo
 * @date 2021/4/14
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentBO {
    private Moment moment;
    private User user;
    private String htmlContent;
    private List<Tag> tagList;

    public static MomentBO fromMomentAndHtmlAndTags(Moment moment, User user, String htmlContent, List<Tag> tagList){
        return MomentBO.builder()
                .moment(moment)
                .user(user)
                .htmlContent(htmlContent)
                .tagList(tagList)
                .build();
    }

}
