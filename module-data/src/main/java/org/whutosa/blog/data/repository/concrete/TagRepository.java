package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.Tag;
import org.whutosa.blog.data.repository.DataRepository;

import java.util.Optional;

/**
 * @author bobo
 * @date 2021/4/14
 */

public interface TagRepository extends DataRepository<Tag, Integer> {
    /**
     * 通过标签名找到标签
     * @param name 标签名字
     * @return 标签
     */
    Optional<Tag> getTagByName(String name);
}
