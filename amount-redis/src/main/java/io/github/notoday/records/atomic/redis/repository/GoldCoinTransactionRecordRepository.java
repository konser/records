package io.github.notoday.records.atomic.redis.repository;

import io.github.notoday.records.atomic.redis.domain.GoldCoinTransactionRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author no-today
 * @date 2021/02/08 上午10:17
 */
public interface GoldCoinTransactionRecordRepository extends MongoRepository<GoldCoinTransactionRecord, String> {

    boolean existsByTraceId(String traceId);

    Optional<GoldCoinTransactionRecord> findOneByTraceId(String traceId);

    long countByUser(String user);
}
