package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.Permission;
import org.whutosa.blog.data.repository.DataRepository;

import java.util.List;

/**
 * @author bobo
 * @date 2021/4/8
 */

public interface PermissionRepository extends DataRepository<Permission, Integer> {
    /**
     * 通过角色名字找到
     * @param name 权限名字
     * @return 权限列表
     */
    List<Permission> findAllByName(String name);
}
