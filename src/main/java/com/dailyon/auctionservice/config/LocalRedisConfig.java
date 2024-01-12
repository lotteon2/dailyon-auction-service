package com.dailyon.auctionservice.config;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Profile({"local"})
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
  ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration(
            Objects.requireNonNull(env.getProperty("spring.redis.host")),
            Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.redis.port"))));
    redisStandaloneConfiguration.setPassword(env.getProperty("spring.redis.password"));
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
  }

  // Redis Atomic Counter to store no. of total messages sent from multiple app instances.
  @Bean
  RedisAtomicInteger chatMessageCounter(RedisConnectionFactory redisConnectionFactory) {
    return new RedisAtomicInteger(ChatConstants.MESSAGE_COUNTER_KEY, redisConnectionFactory);
  }

  // Redis Atomic Counter to store no. of Active Users.
  @Bean
  RedisAtomicLong activeUserCounter(RedisConnectionFactory redisConnectionFactory) {
    return new RedisAtomicLong(ChatConstants.ACTIVE_USER_KEY, redisConnectionFactory);
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
