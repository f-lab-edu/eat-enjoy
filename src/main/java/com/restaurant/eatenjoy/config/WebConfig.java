package com.restaurant.eatenjoy.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restaurant.eatenjoy.util.interceptor.AuthorityInterceptor;
import com.restaurant.eatenjoy.util.interceptor.RestaurantInterceptor;
import com.restaurant.eatenjoy.util.resolver.LoginAuthIdArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginAuthIdArgumentResolver loginAuthIdArgumentResolver;

	private final AuthorityInterceptor authorityInterceptor;

	private final RestaurantInterceptor restaurantInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginAuthIdArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorityInterceptor)
			.excludePathPatterns("/api/*/login", "/api/*/logout", "/api/*/check-mail-token");

		registry.addInterceptor(restaurantInterceptor)
			.addPathPatterns("/api/restaurants/**");
	}

}
