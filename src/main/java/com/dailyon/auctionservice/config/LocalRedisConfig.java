package com.dailyon.auctionservice.config;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessageListener;
import com.dailyon.auctionservice.document.BidHistory;
import com.dailyon.auctionservice.dto.response.BidInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Profile("local")
@Configuration(proxyBeanMethods = false)
public class LocalRedisConfig {

  private final Environment env;

  public LocalRedisConfig(Environment env) {
    this.env = env;
  }

  private Set<RedisNode> parseRedisNodes(String nodes) {
    Set<RedisNode> redisNodes = new HashSet<>();
    for (String node : Objects.requireNonNull(nodes).split(",")) {
      String[] parts = node.split(":");
      redisNodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
    }
    return redisNodes;
  }

  @Bean
  @Primary
  ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration(
            Objects.requireNonNull(env.getProperty("spring.redis.host")),
            Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.redis.port"))));
    redisStandaloneConfiguration.setPassword(env.getProperty("spring.redis.password"));
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean("rsTemplate")
  ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      ReactiveRedisConnectionFactory redisConnectionFactory) {
    return new ReactiveStringRedisTemplate(redisConnectionFactory);
  }

  @Bean("reactiveRedisTemplateForBid")
  public ReactiveRedisTemplate<String, BidInfo> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory factory) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈 등록
    objectMapper.disable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 timestamp가 아닌 ISO 형식으로 출력

    Jackson2JsonRedisSerializer<BidInfo> serializer =
        new Jackson2JsonRedisSerializer<>(BidInfo.class);
    serializer.setObjectMapper(objectMapper);
    RedisSerializationContext<String, BidInfo> serializationContext =
        RedisSerializationContext.<String, BidInfo>newSerializationContext(
                new StringRedisSerializer())
            .value(serializer)
            .build();

    return new ReactiveRedisTemplate<>(factory, serializationContext);
  }

  @Bean
  ApplicationRunner applicationRunner(RedisChatMessageListener redisChatMessageListener) {
    return args -> {
      redisChatMessageListener
          .subscribeMessageChannelAndPublishOnWebSocket()
          .doOnSubscribe(subscription -> log.info("Redis Listener Started"))
          .doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
          .doFinally(signalType -> log.info("Stopped Listener. Signal Type: {}", signalType))
          .subscribe();
    };
  }
}
