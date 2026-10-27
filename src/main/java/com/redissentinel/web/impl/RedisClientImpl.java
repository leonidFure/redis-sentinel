package com.redissentinel.web.impl;

import com.redissentinel.web.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class RedisClientImpl implements RedisClient {

    private final StringRedisTemplate redisTemplate;

    @Override
    public Mono<String> ping() {
        return Mono.fromCallable(() -> redisTemplate.execute((RedisCallback<String>) connection -> connection.ping()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> set(final String key, final String value) {
        return Mono.<Void>fromRunnable(() -> redisTemplate.opsForValue().set(key, value))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> get(final String key) {
        return Mono.fromCallable(() -> redisTemplate.opsForValue().get(key))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> delete(final String key) {
        return Mono.fromCallable(() -> redisTemplate.delete(key))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
