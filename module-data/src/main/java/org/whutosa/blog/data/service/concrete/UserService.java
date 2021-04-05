package org.whutosa.blog.data.service.concrete;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.exception.AssertUtils;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.repository.concrete.UserRepository;
import org.whutosa.blog.data.service.BaseDataService;

import javax.annotation.Resource;

/**
 * @author bobo
 * @date 2021/3/31
 */
@Service
@Slf4j
public class UserService extends BaseDataService<User, String> {
    @Resource
    UserRepository userRepository;

    public void addUser(User user){
        AssertUtils.isNull(userRepository.findUserByAccount(user.getAccount()), new ApplicationException(SystemCodeEnum.ARGUMENT_WRONG, "帐号已存在！"));
        user.setPassword(DigestUtils.sha1Hex(user.getPassword()));
        this.insert(user);
    }

    public boolean match(User user){
        User loginUser = userRepository.findUserByAccountAndPassword(user.getAccount(), DigestUtils.sha1Hex(user.getPassword()));
        return null!=loginUser;
    }
}
