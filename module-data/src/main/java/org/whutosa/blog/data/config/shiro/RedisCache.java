package org.whutosa.blog.data.config.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * @author bobo
 * @date 2021/4/7
 */

public class RedisCache<K, V> implements Cache<K , V> {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public V get(K key) throws CacheException {
        if(redisTemplate.hasKey(key.toString())){
            return (V) redisTemplate.opsForValue().get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        return null;
    }

    @Override
    public V remove(K key) throws CacheException {
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
