package io.github.notoday.records.atomic.redis.service;

@FunctionalInterface
public interface SuccessCallback {

    void callback(String traceId);
}