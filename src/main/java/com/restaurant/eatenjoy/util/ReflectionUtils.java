package com.restaurant.eatenjoy.util;

import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

	public static void setFieldValue(Object obj, String fieldName, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("해당 필드를 찾을 수 없습니다.", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("해당 필드에 접근할 수 없습니다.", e);
		}
	}

}
