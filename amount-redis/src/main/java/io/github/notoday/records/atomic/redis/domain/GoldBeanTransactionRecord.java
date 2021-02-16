package io.github.notoday.records.atomic.redis.domain;

import io.github.notoday.records.atomic.redis.domain.enumeration.GoldBeanTransactionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author no-today
 * @date 2021/02/08 上午10:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document("gold_bean_transaction_record")
public class GoldBeanTransactionRecord extends AbstractTransactionRecord<GoldBeanTransactionType> {

    @Field("number")
    private Double number;
}
