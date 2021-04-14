package org.whutosa.blog.data.service.concrete;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.whutosa.blog.data.entity.MomentTag;
import org.whutosa.blog.data.entity.Tag;
import org.whutosa.blog.data.repository.concrete.MomentTagRepository;
import org.whutosa.blog.data.repository.concrete.TagRepository;
import org.whutosa.blog.data.service.BaseDataService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bobo
 * @date 2021/4/14
 */

@Service
@Slf4j
public class TagService extends BaseDataService<Tag, Integer> {
    @Resource
    TagRepository tagRepository;
    @Resource
    MomentTagRepository momentTagRepository;

    public List<Tag> save(Integer momentId, List<String> tags){
        momentTagRepository.findAllByMomentId(momentId).forEach(momentTag -> {
            tagRepository.deleteById(momentTag.getTagId());
            momentTagRepository.delete(momentTag);
        });
        List<Tag> tagList = tags.stream()
                .map(tagName->tagRepository.getTagByName(tagName)
                        .orElseGet(()->tagRepository.save(new Tag(tagName))))
                .collect(Collectors.toList());
        List<MomentTag> momentTagList = tagList.stream()
                .map(tag -> new MomentTag(momentId, tag.getId()))
                .collect(Collectors.toList());
        momentTagRepository.saveAll(momentTagList);
        return tagList;
    }

    public List<Tag> getAllByMomentId(Integer momentId){
        return momentTagRepository.findAllByMomentId(momentId).stream()
                .map(momentTag -> tagRepository.getOne(momentTag.getTagId()))
                .collect(Collectors.toList());
    }

    public void deleteByMomentId(Integer momentId){
        momentTagRepository.findAllByMomentId(momentId).forEach(momentTag -> {
            momentTagRepository.delete(momentTag);
            Integer tagId = momentTag.getTagId();
            if(momentTagRepository.findAllByTagId(tagId).size()==0){
                tagRepository.deleteById(tagId);
            }
        });
    }

}
