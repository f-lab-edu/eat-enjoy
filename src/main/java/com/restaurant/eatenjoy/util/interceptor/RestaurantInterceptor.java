package com.restaurant.eatenjoy.util.interceptor;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.restaurant.eatenjoy.dto.restaurant.RestaurantInfo;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.service.RestaurantService;
import com.restaurant.eatenjoy.util.security.LoginService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantInterceptor implements HandlerInterceptor {

	private static final String RESTAURANT_ID = "restaurantId";

	private final RestaurantService restaurantService;

	private final LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		return checkLoginOwnerIdAndRestaurantId(request);
	}

	private boolean checkLoginOwnerIdAndRestaurantId(HttpServletRequest request) {
		Long ownerId = loginService.getLoginAuthId();

		Map<String, String> map = (Map<String, String>)request.getAttribute(
			HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		if (map.isEmpty()) {
			return true;
		}

		Long restaurantId = null;
		if (map.containsKey(RESTAURANT_ID)) {
			restaurantId = Long.parseLong(map.get(RESTAURANT_ID));
		}

		if (Objects.isNull(restaurantId)) {
			throw new IllegalArgumentException("@PathVariable의 value가 잘못 설정 되어있습니다.");
		}

		RestaurantInfo restaurantInfo = restaurantService.findById(restaurantId);
		if (!ownerId.equals(restaurantInfo.getOwnerId())) {
			throw new AuthorizationException();
		}

		return true;
	}
}
