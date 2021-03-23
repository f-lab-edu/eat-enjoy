package com.restaurant.eatenjoy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class CacheConfig {

	@Value("${spring.redis.cache.host}")
	private String redisHost;

	@Value("${spring.redis.cache.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisCacheConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

}
