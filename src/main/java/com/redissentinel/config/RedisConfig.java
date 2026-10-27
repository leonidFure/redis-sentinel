package com.redissentinel.config;

import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;

@Configuration
public class RedisConfig {

    private static final String ANNOUNCED_HOST = "host.docker.internal";

    private static final String LOCAL_HOST = "127.0.0.1";

    @Bean
    JedisClientConfigurationBuilderCustomizer redisHostMapper() {
        return builder -> builder.customize(config -> config.hostAndPortMapper(RedisConfig::mapAnnouncedHost));
    }

    private static HostAndPort mapAnnouncedHost(final HostAndPort source) {
        if (!ANNOUNCED_HOST.equals(source.getHost())) {
            return source;
        }
        return new HostAndPort(LOCAL_HOST, source.getPort());
    }
}
