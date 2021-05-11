package com.restaurant.eatenjoy.util.cache;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheNames {

	public static final String USER_MAIL_CERTIFIED = "user:mail:certified";
	public static final String OWNER_MAIL_CERTIFIED = "owner:mail:certified";
	public static final String RESTAURANT = "restaurant";

	@Getter
	public enum TimeToLive {
		USER_MAIL_CERTIFIED(CacheNames.USER_MAIL_CERTIFIED, Duration.ofHours(1)),
		OWNER_MAIL_CERTIFIED(CacheNames.OWNER_MAIL_CERTIFIED, Duration.ofHours(1)),
		RESTAURANT(CacheNames.RESTAURANT, Duration.ofHours(1));

		private final String name;
		private final Duration ttl;

		TimeToLive(String name, Duration ttl) {
			this.name = name;
			this.ttl = ttl;
		}
	}

}
