package com.restaurant.eatenjoy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.siot.IamportRestClient.IamportClient;

@Configuration
public class PaymentConfig {

	@Value("${iamport.rest.api.key}")
	private String apiKey;

	@Value("${iamport.rest.api.secret}")
	private String apiSecret;

	@Bean
	public IamportClient iamportClient() {
		return new IamportClient(apiKey, apiSecret);
	}

}
