package com.restaurant.eatenjoy.util.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.util.security.LoginService;
import com.restaurant.eatenjoy.util.security.UserDetailsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorityInterceptor implements HandlerInterceptor {

	private final LoginService loginService;

	private final List<UserDetailsService> userDetailsServices; 

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		Authority authority = findAuthorityAnnotation((HandlerMethod) handler);
		if (authority == null) {
			return true;
		}
		
		for (UserDetailsService userDetailsService : userDetailsServices) {
			if (authority.value() == userDetailsService.getRole()) {
				loginService.validateAuthority(userDetailsService);
				return true;
			}
		}
		
		return false;
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
