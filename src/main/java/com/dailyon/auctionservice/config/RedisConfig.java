package com.dailyon.auctionservice.config;

import com.dailyon.auctionservice.chat.messaging.RedisChatMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Profile("prod")
@Configuration(proxyBeanMethods = false)
public class RedisConfig {

  private final Environment env;

  public RedisConfig(Environment env) {
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
  public ReactiveRedisConnectionFactory clusterRedisConnectionFactory() {
    RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
    clusterConfiguration.setClusterNodes(
        parseRedisNodes(Objects.requireNonNull(env.getProperty("spring.redis.cluster.nodes"))));
    return new LettuceConnectionFactory(clusterConfiguration);
  }

  @Bean("rsTemplate")
  ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {
    return new ReactiveStringRedisTemplate(connectionFactory);
  }
  // Redis Atomic Counter to store no. of total messages sent from multiple app instances.

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
