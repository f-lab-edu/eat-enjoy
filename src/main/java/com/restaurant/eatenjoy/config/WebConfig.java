package com.restaurant.eatenjoy.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restaurant.eatenjoy.util.interceptor.AuthorityInterceptor;
import com.restaurant.eatenjoy.util.resolver.LoginOwnerIdArgumentResolver;
import com.restaurant.eatenjoy.util.resolver.LoginUserIdArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginUserIdArgumentResolver loginUserIdArgumentResolver;

	private final LoginOwnerIdArgumentResolver loginOwnerIdArgumentResolver;

	private final AuthorityInterceptor authorityInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserIdArgumentResolver);
		resolvers.add(loginOwnerIdArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorityInterceptor)
			.excludePathPatterns("/api/*/login", "/api/*/logout", "/api/*/check-mail-token");
	}

}
