package io.github.notoday.records.redisson.localcache;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy.LOAD;
import static org.redisson.api.LocalCachedMapOptions.SyncStrategy.UPDATE;

@SpringBootTest(classes = RedissonLocalcacheApplication.class)
@ExtendWith({RedisTestContainerExtension.class})
@Slf4j
class LocalCacheMapTests {

    @Autowired
    private RedissonClient redissonClient;

    private RLocalCachedMap<String, String> newLocalCachedMap() {
        LocalCachedMapOptions<String, String> options = LocalCachedMapOptions.defaults();
        options.reconnectionStrategy(LOAD);
        options.syncStrategy(UPDATE);

        return redissonClient.getLocalCachedMap("LOCAL_CACHE", JsonJacksonCodec.INSTANCE, options);
    }

    @Test
    public void syncTest() throws Exception {
        var random = new Random();
        for (int i = 0; i < 10; i++) {
            final int ii = i;
//            new Thread(() -> {
                try {
                    var rLocalCachedMap = newLocalCachedMap();

                    while (true) {
                        TimeUnit.SECONDS.sleep(5);

                        var newVal = UUID.randomUUID().toString();

                        log.info("[SYNC-TEST-{}]: SetValue: {}", ii, newVal);
                        rLocalCachedMap.put("KEY_1", newVal);

                        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000) + 500);

                        var getVal = rLocalCachedMap.getCachedMap().get("KEY_1");
                        log.warn("[SYNC-TEST-{}]: GetValue: {}", ii, getVal);
                    }
                } catch (InterruptedException e) {
                }
//            }).start();
        }

        TimeUnit.MINUTES.sleep(1);
    }
}
