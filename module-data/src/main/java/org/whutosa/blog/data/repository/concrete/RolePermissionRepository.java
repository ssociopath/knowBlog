package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.Permission;
import org.whutosa.blog.data.entity.RolePermission;
import org.whutosa.blog.data.repository.DataRepository;

import java.util.List;

/**
 * @author bobo
 * @date 2021/4/9
 */

public interface RolePermissionRepository extends DataRepository<RolePermission, Integer> {


    List<RolePermission> findAllByRoleId(Integer id);
}
