package org.whutosa.blog.api.controller;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import org.whutosa.blog.api.module.query.MomentQuery;
import org.whutosa.blog.api.module.vo.MomentVO;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.Constant;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.bo.MomentBO;
import org.whutosa.blog.data.config.redis.RedisUtil;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.MomentService;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bobo
 * @date 2021/4/14
 */

@RestController
@RequestMapping("/moment")
public class MomentController {
    @Resource
    MomentService momentService;
    @Resource
    UserService userService;

    /**
     * 更新或上传微博
     * @param momentQuery 微博查询体
     * @return MomentVO
     */
    @PostMapping
    @RequiresAuthentication
    public ApplicationResponse<MomentVO> save(MomentQuery momentQuery){
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        MomentBO momentBO = momentService.saveMoment(momentQuery.toMoment(), momentQuery.getHtmlContent(), momentQuery.getTags(), user);
        String key = Constant.MOMENT_CACHE + momentBO.getMoment().getUserId() + ":" + momentBO.getMoment().getId();
        MomentVO momentVO = MomentVO.fromMomentBO(momentBO);
        RedisUtil.setObject(key, momentVO);
        return ApplicationResponse.succeed(momentVO);
    }

    /**
     * 获取某篇微博
     * @param id 微博id
     * @return MomentVO
     */
    @GetMapping("/{id}")
    @RequiresAuthentication
    public ApplicationResponse<MomentVO> get(@PathVariable("id") Integer id){
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        String key = Constant.MOMENT_CACHE+user.getId()+":"+id;
        MomentVO moment = (MomentVO) RedisUtil.getObject(key);
        return ApplicationResponse.succeed(moment);
    }

    /**
     * 获取当前用户所有微博
     * @return List<MomentVO>
     */
    @GetMapping
    @RequiresAuthentication
    public ApplicationResponse<List<MomentVO>> getAll(){
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        List<MomentVO> momentList = new ArrayList<>();
        RedisUtil.scanValue(Constant.MOMENT_CACHE + user.getId() + ":*", key->{
            momentList.add((MomentVO) RedisUtil.getObject(new String(key)));
        });
        return ApplicationResponse.succeed(momentList);
    }

    /**
     * 删除微博
     * @param id 微博id
     * @return 成功或抛出异常
     */
    @DeleteMapping
    public ApplicationResponse<String> delete(Integer id) {
        momentService.deleteMoment(id);
        return ApplicationResponse.succeed();
    }
}
