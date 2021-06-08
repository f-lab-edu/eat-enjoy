package com.restaurant.eatenjoy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile("dev")
@Configuration
public class AwsS3Config {

	@Value("${cloud.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.s3.endPoint}")
	private String endPoint;

	@Value("${cloud.s3.region}")
	private String region;

	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
			.build();
	}

}
