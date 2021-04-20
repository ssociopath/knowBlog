package org.whutosa.blog.api.controller;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whutosa.blog.common.response.ApplicationResponse;
import org.whutosa.blog.common.response.SystemCodeEnum;
import org.whutosa.blog.common.utils.misc.JwtUtil;
import org.whutosa.blog.data.entity.PunchIn;
import org.whutosa.blog.data.entity.User;
import org.whutosa.blog.data.service.concrete.PunchInService;
import org.whutosa.blog.data.service.concrete.UserService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author bobo
 * @date 2021/4/20
 */

@RestController
@RequestMapping("/punch")
public class PunchInController {
    @Resource
    PunchInService punchInService;
    @Resource
    UserService userService;

    @RequestMapping
    @RequiresAuthentication
    public ApplicationResponse<List<Integer>> getMonthlyPunchIn() {
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        LocalDate today = LocalDate.now();
        PunchIn punchIn = punchInService.getMonthlyPunchIn(user.getId(), LocalDate.now());
        if (punchIn == null) {
            return ApplicationResponse.succeed(new ArrayList<>());
        }
        return ApplicationResponse.succeed(IntStream.range(1, today.getDayOfMonth() + 1)
                .filter(day -> PunchInService.checkHasPunchedIn(punchIn.getDailyBitmap(), day))
                .boxed()
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    @RequiresAuthentication
    public ApplicationResponse<String> punchIn() {
        User user = userService.getUserByAccount(JwtUtil.getCurrentClaim(JwtUtil.ACCOUNT));
        return punchInService.dailyPunchIn(user.getId(), LocalDate.now())
                ? ApplicationResponse.succeed()
                : ApplicationResponse.fail(SystemCodeEnum.FAILURE, "你今天已经签到过了哦");
    }
}
