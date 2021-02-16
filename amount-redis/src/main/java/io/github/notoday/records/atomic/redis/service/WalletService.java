package io.github.notoday.records.atomic.redis.service;

import io.github.notoday.records.atomic.redis.domain.Currency;
import io.github.notoday.records.atomic.redis.domain.GoldBeanTransactionRecord;
import io.github.notoday.records.atomic.redis.domain.GoldCoinTransactionRecord;
import io.github.notoday.records.atomic.redis.domain.TransactionType;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldBeanTransactionType;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldCoinTransactionType;
import io.github.notoday.records.atomic.redis.repository.GoldBeanTransactionRecordRepository;
import io.github.notoday.records.atomic.redis.repository.GoldCoinTransactionRecordRepository;
import io.github.notoday.records.atomic.redis.service.dto.UserWalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.DoubleCodec;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author no-today
 * @date 2021/02/07 下午5:21
 */
@Slf4j
@Service
public class WalletService {

    /**
     * 金币: 消费货币, 整型
     */
    final static String GOLD_COIN = "Wallet:GoldCoin";

    /**
     * 金豆: 提现货币, 浮点型
     */
    final static String GOLD_BEAN = "Wallet:GoldBean";


    /**
     * 交易锁
     */
    private final static String TRANSACTION_LOCK = "TRANSACTION_LOCK:";

    protected final RedissonClient redissonClient;

    private final LuaScriptExecution luaScriptExecution;
    private final GoldCoinTransactionRecordRepository goldCoinTransactionRecordRepository;
    private final GoldBeanTransactionRecordRepository goldBeanTransactionRecordRepository;

    public WalletService(RedissonClient redissonClient, LuaScriptExecution luaScriptExecution, GoldCoinTransactionRecordRepository goldCoinTransactionRecordRepository, GoldBeanTransactionRecordRepository goldBeanTransactionRecordRepository) {
        this.redissonClient = redissonClient;
        this.luaScriptExecution = luaScriptExecution;
        this.goldCoinTransactionRecordRepository = goldCoinTransactionRecordRepository;
        this.goldBeanTransactionRecordRepository = goldBeanTransactionRecordRepository;
    }

    protected RMap<String, Long> getGoldCoinMap() {
        return redissonClient.getMap(GOLD_COIN, LongCodec.INSTANCE);
    }

    protected RMap<String, Double> getGoldBeanMap() {
        return redissonClient.getMap(GOLD_BEAN, DoubleCodec.INSTANCE);
    }

    public long getGoldCoin(String user) {
        return Optional.ofNullable(getGoldCoinMap().get(user)).orElse(0L);
    }

    public double getGoldBean(String user) {
        return Optional.ofNullable(getGoldBeanMap().get(user)).orElse(0D);
    }

    public UserWalletDTO getWallet(String user) {
        return new UserWalletDTO()
                .setGoldCoin(getGoldCoin(user))
                .setGoldBean(getGoldBean(user));
    }

    private Number operationCurrency(String user, TransactionType type, Number number, String traceId, Supplier<Boolean> validRepeat, Supplier<? extends RuntimeException> exceptionSupplier, SuccessCallback successCallback) {
        // 增加宽松、减少严格
        var lock = redissonClient.getLock(number.doubleValue() > 0 ?
                TRANSACTION_LOCK + type.currency() + ":" + user + ":" + traceId :
                TRANSACTION_LOCK + type.currency() + ":" + user);

        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    if (validRepeat.get()) {
                        log.warn("[{}]: 重复执行: user: {}, type: {}, number: {}, traceId: {}", type.currency(), user, type, number, traceId);

                        if (Currency.COIN.equals(type.currency())) {
                            return getGoldCoin(user);
                        } else {
                            return getGoldBean(user);
                        }
                    }

                    var result = luaScriptExecution.addAndGet(type.currency(), user, number, exceptionSupplier);

                    successCallback.callback(traceId);

                    return result;
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("[{}]: 获锁失败: user: {}, type: {}, number: {}, traceId: {}", type.currency(), user, type, number, traceId);
                return -2;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordGoldCoin(String user, GoldCoinTransactionType type, long number, String traceId, Map<String, Object> features) {
        try {
            var record = new GoldCoinTransactionRecord();
            record.setUser(user);
            record.setTransactionType(type);
            record.setNumber(number);
            record.setTraceId(traceId);
            record.setFeatures(features);

            goldCoinTransactionRecordRepository.save(record);
        } catch (Exception exception) {
            log.error("[{}]: 保存记录异常: data: {}, type: {}, number: {}, traceId: {}", type.currency(), user, type, number, traceId);
        }
    }

    public void recordGoldBean(String user, GoldBeanTransactionType type, double number, String traceId, Map<String, Object> features) {
        try {
            var record = new GoldBeanTransactionRecord();
            record.setUser(user);
            record.setTransactionType(type);
            record.setNumber(number);
            record.setTraceId(traceId);
            record.setFeatures(features);

            goldBeanTransactionRecordRepository.save(record);
        } catch (Exception exception) {
            log.error("[{}]: 保存记录异常: data: {}, type: {}, number: {}, traceId: {}", type.currency(), user, type, number, traceId);
        }
    }

    public long operationGoldCoin(String user, GoldCoinTransactionType type, long number, String traceId, Map<String, Object> features) {
        return operationCurrency(user, type, number, traceId,
                () -> goldCoinTransactionRecordRepository.existsByTraceId(traceId),
                () -> new RuntimeException("金币不足"),
                e -> recordGoldCoin(user, type, number, traceId, features)
        ).longValue();
    }

    public double operationGoldBean(String user, GoldBeanTransactionType type, long number, String traceId, Map<String, Object> features) {
        return operationCurrency(user, type, number, traceId,
                () -> goldBeanTransactionRecordRepository.existsByTraceId(traceId),
                () -> new RuntimeException("金豆不足"),
                e -> recordGoldBean(user, type, number, traceId, features)
        ).doubleValue();
    }
}
