package io.github.notoday.records.redisson.localcache.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy.LOAD;
import static org.redisson.api.LocalCachedMapOptions.SyncStrategy.UPDATE;

/**
 * @author no-today
 * @date 2021/02/05 下午2:38
 */
@Slf4j
@Service
public class RedissonLocalcacheExamples {

    private final RedissonClient redissonClient;
    private final RLocalCachedMap<String, String> rLocalCachedMap;

    public RedissonLocalcacheExamples(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;

        LocalCachedMapOptions<String, String> options = LocalCachedMapOptions.defaults();
        options.reconnectionStrategy(LOAD);
        options.syncStrategy(UPDATE);

        rLocalCachedMap = redissonClient.getLocalCachedMap("LOCAL_CACHE", JsonJacksonCodec.INSTANCE, options);
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(5);

                    var val = UUID.randomUUID().toString();
                    log.info("[LOCAL_CACHE_TEST]: SetValue: {}", val);
                    rLocalCachedMap.put("KEY_1", val);

                    TimeUnit.SECONDS.sleep(5);

                    var getVal = rLocalCachedMap.getCachedMap().get("KEY_1");
                    log.warn("[LOCAL_CACHE_TEST]: GetValue: {}", getVal);
                }
            } catch (InterruptedException e) {

            }
        }).start();
    }
}
