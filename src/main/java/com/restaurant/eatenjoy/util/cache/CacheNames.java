package com.restaurant.eatenjoy.util.cache;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheNames {

	public static final String RESTAURANT = "restaurant";

	public static final String MENU_GROUP = "menuGroup";

	@Getter
	public enum TimeToLive {
		RESTAURANT(CacheNames.RESTAURANT, Duration.ofHours(1)),
		MENU_GROUP(CacheNames.MENU_GROUP, Duration.ofHours(1));

		private final String name;
		private final Duration ttl;

		TimeToLive(String name, Duration ttl) {
			this.name = name;
			this.ttl = ttl;
		}
	}

}
