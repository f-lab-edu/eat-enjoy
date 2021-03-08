package com.restaurant.eatenjoy.util.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.restaurant.eatenjoy.annotation.CurrentLoginId;
import com.restaurant.eatenjoy.exception.UnauthorizedException;
import com.restaurant.eatenjoy.service.LoginService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentLoginIdArgumentResolver implements HandlerMethodArgumentResolver {

	private final LoginService loginService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentLoginId.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String loginId = loginService.getLoginId();
		if (loginId == null) {
			throw new UnauthorizedException();
		}

		return loginId;
	}

}
