package org.whutosa.blog.api.module.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.whutosa.blog.data.bo.MomentBO;
import org.whutosa.blog.data.entity.Moment;
import org.whutosa.blog.data.entity.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bobo
 * @date 2021/4/14
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentVO {
    private Integer id;
    private String title;
    private String userName;
    private List<LinkedUrlVO> urls;
    private String htmlContent;
    private List<TagVO> tags;
    private String publishDate;
    private Integer status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TagVO {
        private Integer id;
        private String name;

        public static TagVO fromTag(Tag tag) {
            return new TagVO(tag.getId(), tag.getName());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkedUrlVO {
        private String name;
        private String link;

        public static LinkedUrlVO fromMomentUrl(String momentUrl){
            String[] linkedUrl = momentUrl.split(":");
            return new LinkedUrlVO(linkedUrl[0], linkedUrl[1]);
        }
    }



    public static MomentVO fromMomentBO(MomentBO momentBO) {
        Moment moment = momentBO.getMoment();
        List<LinkedUrlVO> urls = null;
        if(null!=moment.getUrlList()){
            urls = Arrays.stream(moment.getUrlList().split(","))
                    .map(LinkedUrlVO::fromMomentUrl)
                    .collect(Collectors.toList());
        }
        List<TagVO> tagVOList = momentBO.getTagList().stream()
                .map(TagVO::fromTag).collect(Collectors.toList());

        return MomentVO.builder()
                .id(moment.getId())
                .title(moment.getTitle())
                .userName(momentBO.getUser().getUsername())
                .htmlContent(momentBO.getHtmlContent())
                .publishDate(moment.getPublishDate())
                .status(moment.getStatus())
                .tags(tagVOList)
                .urls(urls)
                .build();
    }

}
