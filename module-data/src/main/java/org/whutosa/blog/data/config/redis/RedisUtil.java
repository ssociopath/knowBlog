package org.whutosa.blog.data.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author bobo
 * @date 2021/4/10
 */

@Component
@Slf4j
public class RedisUtil {

    private static RedisTemplate<String, Object> redisTemplate;

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    public static boolean setObject(String key, Object value){
        try{
            redisTemplate.opsForValue().set(key,value);
            return true;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    public static boolean setObject(String key, Object value, Long time){
        try{
            if(time>0){
                redisTemplate.opsForValue().set(key,value, time, TimeUnit.SECONDS);
                return true;
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return false;
    }

    public static boolean hasKey(String key){
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return false;
    }

    public static Object getObject(String key){
        return key==null? null:redisTemplate.opsForValue().get(key);
    }

    public static boolean deleteKey(String key){
        try{
            redisTemplate.delete(key);
            return true;
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return false;
    }
}
