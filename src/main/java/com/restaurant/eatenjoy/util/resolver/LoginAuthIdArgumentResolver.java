package com.restaurant.eatenjoy.util.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.util.security.LoginService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginAuthIdArgumentResolver implements HandlerMethodArgumentResolver {

	private final LoginService loginService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginAuthId.class);
	}

	@Override
	public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		return loginService.getLoginAuthId();
	}

}
