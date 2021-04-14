package org.whutosa.blog.common.utils.misc;

/**
 * 常量类
 * @author bobo
 * @date 2021/4/9
 */

public class Constant {
    /**
     * redis中的refreshToken
     */
    public static final String REDIS_REFRESH_TOKEN = "refreshToken:";

    /**
     * accessToken过期时间（SECOND）
     */
    public static final String ACCESS_TOKEN_EXPIRE_TIME = "300";

    /**
     * refreshToken过期时间（SECOND）
     */
    public static final String REFRESH_TOKEN_EXPIRE_TIME = "1800";

    /**
     * JWT认证加密私钥(Base64加密)
     */
    public static final String ENCRYPT_JWT_KEY = "U0JBUElKV1RkV2FuZzkyNjQ1NA==";

    /**
     * 密码位数
     */
    public static final Integer PASSWORD_MAX_LEN = 8;

    /**
     * redis中的htmlContent
     */
    public static final String MOMENT_CACHE = "momentCache:";
}
