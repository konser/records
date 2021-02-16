package io.github.notoday.records.agora.example.service;

import cn.hutool.core.net.NetUtil;
import com.github.benmanes.caffeine.cache.*;
import io.github.notoday.records.agora.example.config.ApplicationProperties;
import io.github.notoday.records.agora.example.service.listener.IRtmChannelListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RTMConnectionPool {

    /**
     * 闲置 15 分钟就关闭连接
     */
    private static final int UNUSED_MINUTES = 15;

    /**
     * 声网 appId
     */
    private final ApplicationProperties applicationProperties;

    /**
     * RTM 客户端连接池, 间隔一定时间未使用会释放掉连接
     */
    private final LoadingCache<String, RTMConnection> connectionPool;

    private final String robotPrefix;

    public RTMConnectionPool(ApplicationProperties applicationProperties, RTMAuthentication rtmAuthentication) {
        this.applicationProperties = applicationProperties;
        this.robotPrefix = "robot:" + NetUtil.getLocalhostStr() + ":";
        log.info("[RTM-KEY]: {}", this.robotPrefix);

        CacheLoader<String, RTMConnection> loader = (channelName) -> {
            log.info("[RTM] 连接池-创建连接: {}", channelName);

            return new RTMConnection(
                    this.applicationProperties.getAppId(),
                    channelName,
                    this.robotPrefix + channelName,
                    new IRtmChannelListener(channelName),
                    rtmAuthentication);
        };

        this.connectionPool = Caffeine.newBuilder()
                .initialCapacity(applicationProperties.getRtmInitConnectionPoolSize())
                .maximumSize(applicationProperties.getRtmMaxConnectionPoolSize())
                .expireAfterAccess(UNUSED_MINUTES, TimeUnit.MINUTES)
                .scheduler(Scheduler.forScheduledExecutorService(Executors.newSingleThreadScheduledExecutor()))
                .removalListener((String k, RTMConnection v, RemovalCause c) -> Optional.ofNullable(v).ifPresent(RTMConnection::release))
                .build(loader);
    }

    public RTMConnection getConnection(String channelName) {
        return connectionPool.get(channelName);
    }

    @PreDestroy
    public void destroy() {
        log.info("[RTM] 服务下线: 关闭所有连接");
        connectionPool.invalidateAll();
    }

    public void sendChannelMessage(String channelName, String optionType, String msg, Object data) {
        getConnection(channelName).sendChannelMessage(optionType, msg, data);
    }

    public void sendOneToOneMessage(String channelName, List<String> targetUsers, String optionType, String msg, Object data) {
        RTMConnection connection = getConnection(channelName);
        targetUsers.parallelStream()
                .forEach(user -> connection.sendPeerMessage(optionType, msg, data, user));
    }

    /**
     * 发送全频道广播消息
     *
     * @param optionType 消息类型
     * @param msg        提示信息
     * @param data       消息体
     */
    public void sendBroadcastMessage(String optionType, String msg, Object data) {
        Collection<RTMConnection> values = connectionPool.asMap().values();
        log.info("[RTM] 发送全频道广播: msgType: {}, channels: {}", optionType, values.size());
        values.parallelStream()
                .forEach(rtmConnection -> rtmConnection.sendChannelMessage(optionType, msg, data));
    }
}
