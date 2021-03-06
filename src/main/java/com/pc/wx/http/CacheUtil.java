package com.pc.wx.http;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-18 15:23)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component
public class CacheUtil {
    private final String CACHE_HASH = "OPEN";
    private final String CACHE_HASH_NEW = "OPEN_NEW";
    @Autowired
    private JedisPool jedisPool;
    public String getCache(String key) {
        Jedis jedis=jedisPool.getResource();
        String val = jedis.get(key);
        jedis.close();
        return val;
    }

    public void setCache(String key, String val) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, val);
        jedis.close();
    }

    public void setCacheOnExpire(String key, String val,int expire) {
        if(StringUtils.isEmpty(val)) return;
        Jedis jedis = jedisPool.getResource();
        jedis.setex(key,expire, val);
        jedis.close();
    }
    public void delCache(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.close();
    }

    public void hSetCache(String field,String key,String value) {
        if(StringUtils.isEmpty(value)) return;
        Jedis jedis = jedisPool.getResource();
        jedis.hset(field, key, value);
        jedis.close();
    }
    public String hGetCache(String field,String key) {
        Jedis jedis = jedisPool.getResource();
        String val=jedis.hget(field, key);
        jedis.close();
        return val;
    }
}
