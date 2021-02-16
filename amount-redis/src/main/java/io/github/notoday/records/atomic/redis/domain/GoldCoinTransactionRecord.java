package io.github.notoday.records.atomic.redis.domain;

import io.github.notoday.records.atomic.redis.domain.enumeration.GoldCoinTransactionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author no-today
 * @date 2021/02/07 下午4:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document("gold_coin_transaction_record")
public class GoldCoinTransactionRecord extends AbstractTransactionRecord<GoldCoinTransactionType> {

    @Field("number")
    private Long number;
}