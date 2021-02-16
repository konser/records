package io.github.notoday.records.atomic.redis.domain.enumeration;

import io.github.notoday.records.atomic.redis.domain.Currency;
import io.github.notoday.records.atomic.redis.domain.TransactionType;

/**
 * 金币流水类型
 *
 * @author no-today
 * @date 2021/02/07 下午4:58
 */
public enum GoldCoinTransactionType implements TransactionType {

    //
    RECHARGE("充值"),
    SEND_GIFT("送礼消费"),
    EXCHANGE("金豆兑换");

    private final String description;

    GoldCoinTransactionType(String description) {
        this.description = description;
    }


    @Override
    public Currency currency() {
        return Currency.COIN;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
