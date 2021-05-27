package com.restaurant.eatenjoy.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.restaurant.eatenjoy.util.cache.CacheNames.TimeToLive;

@Configuration
@EnableCaching
public class CacheConfig {

	@Value("${spring.redis.cache.host}")
	private String redisHost;

	@Value("${spring.redis.cache.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisCacheConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	@Primary
	public CacheManager redisCacheManager() {
		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisCacheConnectionFactory())
			.cacheDefaults(redisCacheConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap())
			.build();
	}

	@Bean
	public CacheManager simpleCacheManager() {
		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
		simpleCacheManager.setCaches(caffeineCaches());

		return simpleCacheManager;
	}

	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.activateDefaultTyping(
			BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(Object.class)
				.build(),
			ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	private RedisCacheConfiguration redisCacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(RedisSerializationContext
				.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer()));
	}

	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		return Arrays.stream(TimeToLive.values())
			.filter(TimeToLive::isRedisCache)
			.collect(Collectors.toMap(
				TimeToLive::getName,
				timeToLive -> redisCacheConfiguration().entryTtl(timeToLive.getTtl())));
	}

	private List<CaffeineCache> caffeineCaches() {
		return Arrays.stream(TimeToLive.values())
			.filter(timeToLive -> !timeToLive.isRedisCache())
			.map(timeToLive -> new CaffeineCache(
				timeToLive.getName(),
				Caffeine.newBuilder()
					.expireAfterWrite(timeToLive.getTtl())
					.build()))
			.collect(Collectors.toList());
	}

}
