package com.redissentinel.web;

import reactor.core.publisher.Mono;

public interface RedisClient {

    Mono<String> ping();

    Mono<Void> set(String key, String value);

    Mono<String> get(String key);

    Mono<Boolean> delete(String key);
}
