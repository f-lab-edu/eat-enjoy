package com.restaurant.eatenjoy.util.cache;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheNames {

	public static final String RESTAURANT = "restaurant";

	@Getter
	public enum TimeToLive {
		RESTAURANT(CacheNames.RESTAURANT, Duration.ofHours(1));

		private final String name;
		private final Duration ttl;

		TimeToLive(String name, Duration ttl) {
			this.name = name;
			this.ttl = ttl;
		}
	}

}
