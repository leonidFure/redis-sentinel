package com.redissentinel.service;

import reactor.core.publisher.Mono;

public interface RedisWriteService {

    Mono<Void> write();
}
