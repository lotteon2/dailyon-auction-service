package com.dailyon.auctionservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Configuration
public class DynamoDbConfig {
    @Value("${cloud.aws.dynamodb.endpoint:dynamodb.ap-northeast-2.amazonaws.com}")
    private String endpoint;

    @Value("${cloud.aws.credentials.ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${cloud.aws.credentials.SECRET_ACCESS_KEY}")
    private String secretKey;

    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    public AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(awsCredentials());
    }

    @Bean
    @Primary
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    @Primary
    AmazonDynamoDBAsync amazonDynamoDBAsync() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endpoint, "ap-northeast-2");

        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withCredentials(awsCredentialsProvider())
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Bean
    @Primary
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDBAsync amazonDynamoDBAsync, DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDBAsync, config);
    }

    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<Date, LocalDateTime> {
        @Override
        public Date convert(LocalDateTime object) {
            return Date.from(object.toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDateTime unconvert(Date object) {
            return LocalDateTime.ofInstant(object.toInstant(), ZoneId.of("UTC"));
        }
    }
}
