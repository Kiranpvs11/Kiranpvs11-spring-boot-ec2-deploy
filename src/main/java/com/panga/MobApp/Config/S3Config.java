package com.panga.MobApp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	
	
	@Bean
	public AmazonS3 amazonS3(S3Properties props) {
	    System.out.println("🧪 ACCESS KEY: " + props.getAccessKey());
	    System.out.println("🧪 SECRET KEY: " + props.getSecretKey());
	    System.out.println("🧪 REGION: " + props.getRegion());
	    System.out.println("🧪 BUCKET: " + props.getS3().getBucketName());

	    if (props.getRegion() == null) {
	        throw new RuntimeException("🔥 REGION is null. Config binding failed.");
	    }

	    BasicAWSCredentials credentials = new BasicAWSCredentials(
	            props.getAccessKey(), props.getSecretKey());

	    return AmazonS3ClientBuilder.standard()
	            .withRegion(props.getRegion())
	            .withCredentials(new AWSStaticCredentialsProvider(credentials))
	            .build();
	}

}
