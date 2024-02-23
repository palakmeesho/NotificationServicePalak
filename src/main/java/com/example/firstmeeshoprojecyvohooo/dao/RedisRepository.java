package com.example.firstmeeshoprojecyvohooo.dao;

import com.example.firstmeeshoprojecyvohooo.model.RedisBlacklist;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisRepository {
    private HashOperations hashOperations;

    public  RedisRepository(RedisTemplate redisTemplate)
    {
        this.hashOperations = redisTemplate.opsForHash();
    }
    public void save(RedisBlacklist redisBlacklist)
    {
        hashOperations.put("HASH",redisBlacklist.getPhoneNumber(),redisBlacklist);
    }
    public RedisBlacklist findByPhoneNumber(Long phoneNumber)
    {
        return (RedisBlacklist) hashOperations.get("HASH",phoneNumber);
    }
    public List<RedisBlacklist> findAll()
    {
        return hashOperations.values("HASH");
    }

}
