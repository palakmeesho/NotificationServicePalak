package com.example.firstmeeshoprojecyvohooo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public JedisConnectionFactory connectionFactory()
    {
        //jedisConnectionFactory.getConnection().flushDb();
        return new JedisConnectionFactory();
    }
    @Bean
    public RedisTemplate redisTemplate()
    {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory());
        return redisTemplate;
    }
}