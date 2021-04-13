package com.restaurant.eatenjoy.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.restaurant.eatenjoy.util.cache.CacheNames;

@Configuration
@EnableCaching
public class CacheConfig {

	private static final GenericJackson2JsonRedisSerializer GENERIC_JACKSON_2_JSON_REDIS_SERIALIZER = new GenericJackson2JsonRedisSerializer();

	@Value("${spring.redis.cache.host}")
	private String redisHost;

	@Value("${spring.redis.cache.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisCacheConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	public CacheManager redisCacheManager() {
		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisCacheConnectionFactory())
			.cacheDefaults(redisCacheConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap())
			.build();
	}

	private RedisCacheConfiguration redisCacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(RedisSerializationContext
				.SerializationPair.fromSerializer(GENERIC_JACKSON_2_JSON_REDIS_SERIALIZER));
	}

	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		for (CacheNames.TimeToLive timeToLive : CacheNames.TimeToLive.values()) {
			cacheConfigurations.put(timeToLive.getName(), redisCacheConfiguration().entryTtl(timeToLive.getTtl()));
		}

		return cacheConfigurations;
	}

}
