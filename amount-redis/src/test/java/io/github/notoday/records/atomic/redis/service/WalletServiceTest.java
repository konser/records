package io.github.notoday.records.atomic.redis.service;

import io.github.notoday.records.atomic.redis.AmountRedisApplication;
import io.github.notoday.records.atomic.redis.RedisTestContainerExtension;
import io.github.notoday.records.atomic.redis.domain.TransactionType;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldBeanTransactionType;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldCoinTransactionType;
import io.github.notoday.records.atomic.redis.repository.GoldBeanTransactionRecordRepository;
import io.github.notoday.records.atomic.redis.repository.GoldCoinTransactionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author no-today
 * @date 2021/02/08 下午2:06
 */
@ExtendWith({RedisTestContainerExtension.class})
@SpringBootTest(classes = AmountRedisApplication.class)
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

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

    private static final String[] USERS = new String[]{
            USER_0,
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
    void getGoldCoin() {
        assertEquals(0, walletService.getGoldCoin(USER_0));
    }

    @Test
    void getGoldBean() {
        assertEquals(0, walletService.getGoldBean(USER_0));
    }

    @Test
    void getWallet() {
        var wallet = walletService.getWallet(USER_0);
        assertEquals(0, wallet.getGoldCoin());
        assertEquals(0, wallet.getGoldBean());
    }

    @Test
    void recordGoldCoin() {
        var traceId = UUID.randomUUID().toString();
        walletService.recordGoldCoin(USER_0, GoldCoinTransactionType.RECHARGE, 100, traceId, Map.of("amount", 1, "channel", "google"));

        validGoldCoinRecord(USER_0, traceId, 100, GoldCoinTransactionType.RECHARGE, 2);
    }

    @Test
    void recordGoldBean() {
        var traceId = UUID.randomUUID().toString();
        walletService.recordGoldBean(USER_0, GoldBeanTransactionType.CASH_OUT, 100, traceId, Map.of("exchange_rate", 0.35));

        validGoldBeanRecord(USER_0, traceId, 100, GoldBeanTransactionType.CASH_OUT, 1);
    }

    @Test
    void operationGoldCoin() {
        assertEquals(assertThrows(RuntimeException.class, () -> walletService.operationGoldCoin(USER_0, GoldCoinTransactionType.RECHARGE, -1, UUID.randomUUID().toString(), Map.of("amount", 1, "channel", "google"))).getMessage(), "金币不足");

        var coinTraceId = UUID.randomUUID().toString();
        assertEquals(100, walletService.operationGoldCoin(USER_0, GoldCoinTransactionType.RECHARGE, 100, coinTraceId, Map.of("amount", 1, "channel", "google")));
        assertEquals(100, walletService.operationGoldCoin(USER_0, GoldCoinTransactionType.RECHARGE, 100, coinTraceId, Map.of("amount", 1, "channel", "google")));
    }

    @Test
    void operationGoldBean() {
        assertEquals(assertThrows(RuntimeException.class, () -> walletService.operationGoldBean(USER_0, GoldBeanTransactionType.CASH_OUT, -1, UUID.randomUUID().toString(), Map.of("exchange_rate", 0.35))).getMessage(), "金豆不足");

        var beanTraceId = UUID.randomUUID().toString();
        assertEquals(100, walletService.operationGoldBean(USER_0, GoldBeanTransactionType.CASH_OUT, 100, beanTraceId, Map.of("exchange_rate", 0.35)));
        assertEquals(100, walletService.operationGoldBean(USER_0, GoldBeanTransactionType.CASH_OUT, 100, beanTraceId, Map.of("exchange_rate", 0.35)));
    }

    @Test
    void normalTest() {
        for (String user : USERS) {
            assertEquals(0, walletService.getGoldCoin(user));
            assertEquals(0, walletService.getGoldBean(user));

            assertEquals(assertThrows(RuntimeException.class, () -> walletService.operationGoldCoin(user, GoldCoinTransactionType.RECHARGE, -1, UUID.randomUUID().toString(), Map.of("amount", 1, "channel", "google"))).getMessage(), "金币不足");
            assertEquals(assertThrows(RuntimeException.class, () -> walletService.operationGoldBean(user, GoldBeanTransactionType.CASH_OUT, -1, UUID.randomUUID().toString(), Map.of("exchange_rate", 0.35))).getMessage(), "金豆不足");

            var coinTraceId = UUID.randomUUID().toString();
            var beanTraceId = UUID.randomUUID().toString();

            assertEquals(100, walletService.operationGoldCoin(user, GoldCoinTransactionType.RECHARGE, 100, coinTraceId, Map.of("amount", 1, "channel", "google")));
            assertEquals(100, walletService.operationGoldBean(user, GoldBeanTransactionType.CASH_OUT, 100, beanTraceId, Map.of("exchange_rate", 0.35)));

            validGoldCoinRecord(user, coinTraceId, 100, GoldCoinTransactionType.RECHARGE, 2);
            validGoldBeanRecord(user, beanTraceId, 100, GoldBeanTransactionType.CASH_OUT, 1);
        }
    }

    private void validGoldCoinRecord(String user, String coinTraceId, long number, TransactionType transactionType, int featureSize) {
        var existsGoldCoinTransactionRecord = goldCoinTransactionRecordRepository.findOneByTraceId(coinTraceId);
        assertTrue(existsGoldCoinTransactionRecord.isPresent());
        existsGoldCoinTransactionRecord.ifPresent(record -> {
            assertEquals(record.getUser(), user);
            assertEquals(record.getNumber(), number);
            assertEquals(record.getTransactionType(), transactionType);
            assertEquals(record.getFeatures().size(), featureSize);
        });
    }

    private void validGoldBeanRecord(String user, String beanTraceId, double number, TransactionType transactionType, int featureSize) {
        var existsGoldBeanTransactionRecord = goldBeanTransactionRecordRepository.findOneByTraceId(beanTraceId);
        assertTrue(existsGoldBeanTransactionRecord.isPresent());
        existsGoldBeanTransactionRecord.ifPresent(record -> {
            assertEquals(record.getUser(), user);
            assertEquals(record.getNumber(), number);
            assertEquals(record.getTransactionType(), transactionType);
            assertEquals(record.getFeatures().size(), featureSize);
        });
    }
}