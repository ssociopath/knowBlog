package org.whutosa.blog.data.service.concrete;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.whutosa.blog.common.utils.misc.Constant;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.bo.MomentBO;
import org.whutosa.blog.data.config.redis.RedisUtil;
import org.whutosa.blog.data.entity.Moment;
import org.whutosa.blog.data.entity.Tag;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.repository.concrete.UserRepository;
import org.whutosa.blog.data.service.BaseDataService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @author bobo
 * @date 2021/4/14
 */

@Service
@Slf4j
public class MomentService extends BaseDataService<Moment, Integer> {
    @Resource
    UserRepository userRepository;
    @Resource
    TagService tagService;

    @Transactional(rollbackOn = Exception.class)
    public MomentBO saveMoment(Moment moment, String htmlContent, String[] tags) throws Exception{
        //TODO 发布时间，编辑时间区分
        User user = userRepository.findUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        if(user!=null){
            moment.setUserId(user.getId());
            moment = save(moment);
            int momentId = moment.getId();
            List<Tag> tagList = tagService.save(momentId, Arrays.asList(tags));
            return MomentBO.fromMomentAndHtmlAndTags(moment, user, htmlContent, tagList);
        }
        return null;
    }

    public void deleteMoment(Integer momentId){
        Moment moment = this.getOneOr(momentId, null);
        if(null!=moment){
            this.delete(moment);
            tagService.deleteByMomentId(momentId);
            RedisUtil.deleteKey(Constant.MOMENT_CACHE + moment.getUserId() + ":" + moment.getId());
        }
    }


}
