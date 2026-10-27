package com.redissentinel.service.impl;

import com.redissentinel.service.RedisWriteService;
import com.redissentinel.web.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisWriteServiceImpl implements RedisWriteService {

    private static final String KEY = "schedule:message";

    private final RedisClient redisClient;

    @Override
    public Mono<Void> write() {
        final String value = Instant.now().toString();
        return redisClient.set(KEY, value)
                .doOnSuccess(ignored -> log.info("Wrote Redis key {}={}", KEY, value))
                .doOnError(error -> log.warn("Failed to write Redis key {}", KEY, error));
    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.SECONDS)
    public Mono<Void> writeOnSchedule() {
        return write();
    }
}
