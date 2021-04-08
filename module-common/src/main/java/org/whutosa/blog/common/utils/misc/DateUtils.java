package org.whutosa.blog.common.utils.misc;

import org.apache.shiro.authc.UsernamePasswordToken;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author bobo
 * @date 2021/3/31
 */

public class DateUtils {
    public static final ZoneOffset DEFAULT_ZONE = ZoneOffset.of("+8");

    public static long toMillisecond(LocalDateTime time) {
        return time.toEpochSecond(DEFAULT_ZONE) * 1000L;
    }

    public static LocalDateTime fromSecond(long time) {
        return LocalDateTime.ofEpochSecond(time, 0, DEFAULT_ZONE);
    }

    public static LocalDateTime fromMillisecond(long time) {
        return LocalDateTime.ofEpochSecond(time / 1000, (int) (time % 1000), DEFAULT_ZONE);
    }
}