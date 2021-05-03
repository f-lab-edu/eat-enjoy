package com.restaurant.eatenjoy.util.interceptor;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.restaurant.eatenjoy.annotation.OwnersRestaurantCheck;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.exception.UnauthorizedException;
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

		OwnersRestaurantCheck ownersRestaurantCheck = findAnnotation((HandlerMethod)handler);
		if (ownersRestaurantCheck == null) {
			// 어노테이션이 없으면 인터셉터를 빠져나간다
			return true;
		}

		return checkLoginOwnerIdAndRestaurantId(request);
	}

	private boolean checkLoginOwnerIdAndRestaurantId(HttpServletRequest request) {

		// 현재 로그인한 사장님 아이디 체크
		Long ownerId = loginService.getLoginAuthId();

		// @PathVariable로 넘어온 레스토랑 아이디 체크
		Map<String, String> map = (Map<String, String>)request.getAttribute(
			HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		Long restaurantId = null;
		if (map.containsKey(RESTAURANT_ID)) {
			restaurantId = Long.parseLong(map.get(RESTAURANT_ID));
		}

		// 잘못된 PathVariable 값을 지정할 경우 에외가 발생한다
		if (Objects.isNull(restaurantId)) {
			throw new IllegalArgumentException("@PathVariable의 value가 잘못 설정 되어있습니다.");
		}

		// 자신의 식당이 아닌 정보에 접근 했을때 Exception 발생
		RestaurantInfo restaurantInfo = restaurantService.findById(restaurantId);
		if (!ownerId.equals(restaurantInfo.getOwnerId())) {
			throw new UnauthorizedException();
		}

		return true;
	}

	private OwnersRestaurantCheck findAnnotation(HandlerMethod handlerMethod) {
		if (handlerMethod.hasMethodAnnotation(OwnersRestaurantCheck.class)) {
			return handlerMethod.getMethodAnnotation(OwnersRestaurantCheck.class);
		}

		Class<?> targetClass = handlerMethod.getBean().getClass();
		if (targetClass.isAnnotationPresent(OwnersRestaurantCheck.class)) {
			return targetClass.getDeclaredAnnotation(OwnersRestaurantCheck.class);
		}

		return null;
	}
}
