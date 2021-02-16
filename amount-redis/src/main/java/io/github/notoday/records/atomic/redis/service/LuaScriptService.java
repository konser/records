package io.github.notoday.records.atomic.redis.service;

import io.github.notoday.records.atomic.redis.domain.enumeration.GoldBeanTransactionType;
import io.github.notoday.records.atomic.redis.domain.enumeration.GoldCoinTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author no-today
 * @date 2021/02/08 下午5:41
 */
@Slf4j
@Service
public class LuaScriptService {

    private final LuaScriptExecution luaScriptExecution;
    private final WalletService walletService;

    public LuaScriptService(LuaScriptExecution luaScriptExecution, WalletService walletService) {
        this.luaScriptExecution = luaScriptExecution;
        this.walletService = walletService;
    }

    /**
     * 送礼
     *
     * @param sender            发送方
     * @param receivers         接收方
     * @param count             数量
     * @param deduction         扣除金币(单价,正数)
     * @param increase          增加金豆(单价)
     * @param freeCount         免费数量
     * @param exceptionSupplier 异常
     * @param features          需要存储到DB的特性字段
     */
    public void sendGift(String sender, Collection<String> receivers, int count, long deduction, long increase, int freeCount, Supplier<? extends RuntimeException> exceptionSupplier, Map<String, Object> features) {
        var senderDecr = ((deduction * receivers.size() * count) - (deduction * freeCount)) * -1;
        var receiveIncr = increase * count;

        luaScriptExecution.sendGift(sender, receivers, senderDecr, receiveIncr, exceptionSupplier, traceId -> {
            // TODO 实际场景应该通过消息队列异步处理, 提高性能, 保证最终一致性
            walletService.recordGoldCoin(sender, GoldCoinTransactionType.SEND_GIFT, senderDecr, traceId, features);
            receivers.forEach(receiver -> walletService.recordGoldBean(receiver, GoldBeanTransactionType.RECEIVE_GIFT, receiveIncr, traceId + ":" + receiver, features));
        });
    }

    /**
     * 金豆兑换金币
     *
     * @param user              用户
     * @param deduction         扣除金豆(正数)
     * @param increase          增加金币
     * @param exceptionSupplier 异常
     * @param features          需要存储到DB的特性字段
     */
    public void exchange(String user, long deduction, long increase, Supplier<? extends RuntimeException> exceptionSupplier, Map<String, Object> features) {
        var number = deduction * -1;
        luaScriptExecution.exchange(user, number, increase, exceptionSupplier, traceId -> {
            // TODO 实际场景应该通过消息队列异步处理, 提高性能, 保证最终一致性
            walletService.recordGoldBean(user, GoldBeanTransactionType.EXCHANGE, number, traceId, features);
            walletService.recordGoldCoin(user, GoldCoinTransactionType.EXCHANGE, increase, traceId, features);
        });
    }
}
