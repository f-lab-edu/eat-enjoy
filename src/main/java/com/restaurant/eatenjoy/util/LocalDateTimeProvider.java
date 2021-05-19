package com.restaurant.eatenjoy.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeProvider {

	private static Clock clock = Clock.systemDefaultZone();

	public static LocalDate dateOfNow() {
		return LocalDate.now(clock);
	}

	public static LocalTime timeOfNow() {
		return LocalTime.now(clock);
	}

	public static void mockLocalDateAt(LocalDate localDate) {
		clock = Clock.fixed(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
	}
	
	public static void mockLocalTimeAt(LocalTime localTime) {
		clock = Clock.fixed(localTime.atDate(dateOfNow()).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
	}

	public static void resetClock() {
		clock = Clock.systemDefaultZone();
	}

}
