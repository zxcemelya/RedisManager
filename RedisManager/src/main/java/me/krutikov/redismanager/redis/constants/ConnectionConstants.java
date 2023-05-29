package me.krutikov.redismanager.redis.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConnectionConstants {

    DOMAIN("127.0.0.1"),
    PASSWORD("ыы пашел нахуй");

    private final String value;
}
