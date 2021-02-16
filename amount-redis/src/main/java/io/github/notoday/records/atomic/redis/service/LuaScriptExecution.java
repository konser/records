package io.github.notoday.records.atomic.redis.service;

import io.github.notoday.records.atomic.redis.domain.Currency;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author no-today
 * @date 2021/02/08 下午5:12
 */
@Slf4j
@Component
public class LuaScriptExecution implements LuaScript {

    private static final BigDecimal FAIL_TAG = BigDecimal.valueOf(-1);

    private final RedissonClient redissonClient;

    public LuaScriptExecution(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 原子增减
     *
     * @param currency          货币
     * @param user              用户
     * @param number            数值
     * @param exceptionSupplier 余额不足时抛出的异常
     * @return 操作后的余额
     */
    public Number addAndGet(Currency currency, String user, Number number, Supplier<? extends RuntimeException> exceptionSupplier) {
        var execResult = redissonClient.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                SCRIPT_INCR_DECR,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(Currency.COIN.equals(currency) ? WalletService.GOLD_COIN : WalletService.GOLD_BEAN),
                user,
                new BigDecimal(number.toString()).toPlainString()
        );

        var result = new BigDecimal(Optional.ofNullable(execResult).map(Object::toString).orElse("0"));

        log.info("[{}]: 执行结果: user: {}, number: {}, exec: {}", currency, user, number, result);

        if (FAIL_TAG.equals(result)) {
            // 异常函数为空表示不抛出异常
            if (Objects.nonNull(exceptionSupplier)) {
                throw exceptionSupplier.get();
            } else {
                return -1;
            }
        }

        return result;
    }

    /**
     * 金豆兑换金币
     *
     * @param user              用户
     * @param deduction         减少的金豆(外部计算)
     * @param increase          增减的金币
     * @param exceptionSupplier 金币不足时抛出的异常
     * @param successCallback   成功回调
     */
    public void exchange(String user, long deduction, long increase, Supplier<? extends RuntimeException> exceptionSupplier, SuccessCallback successCallback) {
        var result = redissonClient.getScript(IntegerCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                SCRIPT_EXCHANGE,
                RScript.ReturnType.INTEGER,
                Arrays.asList(WalletService.GOLD_COIN, WalletService.GOLD_BEAN),
                user,
                deduction,
                increase
        );

        if (result.equals(FAIL_TAG.longValue())) {
            throw exceptionSupplier.get();
        } else {
            successCallback.callback(user + ":" + System.currentTimeMillis());
        }
    }

    /**
     * 送礼
     * <p>
     * 发送方扣除金币, 接收方增加金豆
     *
     * <p>
     * 货币说明:
     * <p>
     * 1）金币: 充值得到, 做为消费货币存在, 可以送礼、购买装饰等等...
     * 2）金豆: 用户间消费得到, 例如 A 消费金币 送礼给 B, B 得到金豆, 主要做为提现货币存在
     *
     * @param sender      送礼人
     * @param receivers   收礼人
     * @param senderDecr  发送者总扣除金币
     * @param receiveIncr 每个接受者总增减金豆
     */
    public void sendGift(String sender, Collection<String> receivers, long senderDecr, long receiveIncr, Supplier<? extends RuntimeException> exceptionSupplier, SuccessCallback successCallback) {
        var result = redissonClient.getScript(LongCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                SCRIPT_SEND_GIFT,
                RScript.ReturnType.INTEGER,
                Arrays.asList(WalletService.GOLD_COIN, WalletService.GOLD_BEAN, sender, senderDecr, receiveIncr),
                receivers.toArray()
        );

        if (result.equals(FAIL_TAG.longValue())) {
            throw exceptionSupplier.get();
        } else {
            successCallback.callback(sender + ":" + System.currentTimeMillis());
        }
    }
}
