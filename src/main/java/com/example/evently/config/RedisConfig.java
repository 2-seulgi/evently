package com.example.evently.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching // 캐시 적용
public class RedisConfig {

    @Bean
    public RedisConnectionFactory  redisConnectionFactory() {
        return new LettuceConnectionFactory(); // Redis 연결 팩토리(Lettuce)
    }

    // Key-Value 형태로 데이터를 저장하고 조회
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // redis 서버와 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key를 String 형태로 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value를 JSON 형태로 저장 (객체 직렬화 지원)
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }


}
