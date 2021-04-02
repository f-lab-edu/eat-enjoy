package com.restaurant.eatenjoy.util.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MenuGroupInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		// TODO 해당 레스토랑 존재 및 사장 유무 검증 기능
		// 	레스토랑 기능 develop 브랜치에서 머지 후 진행 예정

		return true;
	}

}
