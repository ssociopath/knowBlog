package org.whutosa.blog.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whutosa.blog.common.exception.ApplicationException;
import org.whutosa.blog.common.response.SystemCodeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bobo
 * @date 2021/4/9
 */

@RestController
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/throw")
    public void rethrow(HttpServletRequest request) {
        String msg = (String)request.getAttribute("filter.error.msg");
        String code = (String)request.getAttribute("filter.error.code");
        throw new ApplicationException(SystemCodeEnum.valueOf(code), msg);
    }
}
