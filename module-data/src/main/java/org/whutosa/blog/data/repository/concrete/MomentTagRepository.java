package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.MomentTag;
import org.whutosa.blog.data.repository.DataRepository;

import java.util.List;

/**
 * @author bobo
 * @date 2021/4/14
 */

public interface MomentTagRepository extends DataRepository<MomentTag, Integer> {

    /**
     * 通过微博id查找文章标签关系
     * @param momentId 微博id
     * @return 返回找到的文章标签列表
     */
    List<MomentTag> findAllByMomentId(Integer momentId);

    /**
     * 通过微博id查找文章标签关系
     * @param tagId 标签id
     * @return 返回找到的文章标签列表
     */
    List<MomentTag> findAllByTagId(Integer tagId);
}
