package io.github.notoday.records.atomic.redis.service;

import io.github.notoday.records.atomic.redis.AmountRedisApplication;
import io.github.notoday.records.atomic.redis.RedisTestContainerExtension;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldBeanTransactionType;
import io.github.notoday.records.atomic.redis.repository.GoldBeanTransactionRecordRepository;
import io.github.notoday.records.atomic.redis.repository.GoldCoinTransactionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author no-today
 * @date 2021/02/08 下午5:58
 */
@ExtendWith({RedisTestContainerExtension.class})
@SpringBootTest(classes = AmountRedisApplication.class)
class LuaScriptServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private LuaScriptService luaScriptService;

    @Autowired
    private GoldCoinTransactionRecordRepository goldCoinTransactionRecordRepository;

    @Autowired
    private GoldBeanTransactionRecordRepository goldBeanTransactionRecordRepository;

    private static final String USER_0 = "user-0";
    private static final String USER_1 = "user-1";
    private static final String USER_2 = "user-2";
    private static final String USER_3 = "user-3";
    private static final String USER_4 = "user-4";
    private static final String USER_5 = "user-5";

    private static final String[] OTHER_USERS = new String[]{
            USER_1,
            USER_2,
            USER_3,
            USER_4,
            USER_5
    };

    @BeforeEach
    void beforeEach() {
        walletService.getGoldCoinMap().delete();
        walletService.getGoldBeanMap().delete();

        goldCoinTransactionRecordRepository.deleteAll();
        goldBeanTransactionRecordRepository.deleteAll();
    }

    @Test
    void sendGift() {
        var message = "金币不足";

        var count = 1;
        var increase = 100;
        var deduction = 100;

        assertEquals(assertThrows(RuntimeException.class, () -> luaScriptService.sendGift(USER_0, Arrays.asList(OTHER_USERS), count, deduction, increase, 0, () -> new RuntimeException(message), null)).getMessage(), message);
        luaScriptService.sendGift(USER_0, Arrays.asList(OTHER_USERS), count, deduction, increase, OTHER_USERS.length, () -> new RuntimeException(message), null);

        assertEquals(0, walletService.getGoldCoin(USER_0));
        assertEquals(0, walletService.getGoldBean(USER_0));
        assertEquals(1, goldCoinTransactionRecordRepository.countByUser(USER_0));
        assertEquals(0, goldBeanTransactionRecordRepository.countByUser(USER_0));

        for (String user : OTHER_USERS) {
            assertEquals(0, walletService.getGoldCoin(user));
            assertEquals(increase, walletService.getGoldBean(user));

            assertEquals(0, goldCoinTransactionRecordRepository.countByUser(user));
            assertEquals(1, goldBeanTransactionRecordRepository.countByUser(user));
        }
    }

    @Test
    void exchange() {
        var message = "金豆不足";

        assertEquals(assertThrows(RuntimeException.class, () -> luaScriptService.exchange(USER_0, 1, 1, () -> new RuntimeException(message), null)).getMessage(), message);

        walletService.operationGoldBean(USER_0, GoldBeanTransactionType.RECEIVE_GIFT, 100, UUID.randomUUID().toString(), null);
        luaScriptService.exchange(USER_0, 100, 100, () -> new RuntimeException(message), null);

        assertEquals(0, walletService.getGoldBean(USER_0));
        assertEquals(100, walletService.getGoldCoin(USER_0));
    }
}