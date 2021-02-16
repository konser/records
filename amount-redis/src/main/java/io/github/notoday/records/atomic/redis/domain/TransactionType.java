package io.github.notoday.records.atomic.redis.domain;

/**
 * @author no-today
 * @date 2021/02/08 上午10:09
 */
public interface TransactionType {

    Currency currency();

    String getName();

    String getDescription();
}
