package org.whutosa.blog.api.config.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author bobo
 * @date 2021/4/7
 */

public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
