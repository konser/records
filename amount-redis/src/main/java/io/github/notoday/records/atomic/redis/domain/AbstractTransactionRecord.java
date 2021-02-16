package io.github.notoday.records.atomic.redis.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * @author no-today
 * @date 2021/02/08 上午10:08
 */
@Data
public abstract class AbstractTransactionRecord<T extends TransactionType> {

    @Id
    private String id;

    @Field("user")
    private String user;

    /**
     * 可追溯、防重复、幂等用途
     */
    @Indexed(unique = true)
    @Field("trace_id")
    private String traceId;

    @Field("transaction_type")
    private T transactionType;

    @Field("created_date")
    private Instant createdDate;

    @Field("features")
    private Map<String, Object> features;
}
