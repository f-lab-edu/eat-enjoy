package com.restaurant.eatenjoy.util.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.service.LoginService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorityInterceptor implements HandlerInterceptor {

	private final LoginService loginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		Authority authority = findAuthorityAnnotation((HandlerMethod) handler);
		if (authority == null) {
			return true;
		}

		switch (authority.value()) {
			case USER:
				loginService.validateUserAuthority();
				break;
			case OWNER:
				loginService.validateOwnerAuthority();
				break;
		}

		return true;
	}

	private Authority findAuthorityAnnotation(HandlerMethod handlerMethod) {
		if (handlerMethod.hasMethodAnnotation(Authority.class)) {
			return handlerMethod.getMethodAnnotation(Authority.class);
		}

		Class<?> targetClass = handlerMethod.getBean().getClass();
		if (targetClass.isAnnotationPresent(Authority.class)) {
			return targetClass.getDeclaredAnnotation(Authority.class);
		}

		return null;
	}

}
