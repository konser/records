package io.github.notoday.records.atomic.redis.domain.enumeration;

import io.github.notoday.records.atomic.redis.domain.Currency;
import io.github.notoday.records.atomic.redis.domain.TransactionType;

/**
 * 金币流水类型
 *
 * @author no-today
 * @date 2021/02/07 下午4:58
 */
public enum GoldBeanTransactionType implements TransactionType {

    //
    RECEIVE_GIFT("收到礼物"),
    EXCHANGE("兑换金币"),
    CASH_OUT("提现");

    private final String description;

    GoldBeanTransactionType(String description) {
        this.description = description;
    }


    @Override
    public Currency currency() {
        return Currency.BEAN;
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
