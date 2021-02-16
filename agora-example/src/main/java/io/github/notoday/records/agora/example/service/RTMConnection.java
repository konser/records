package io.github.notoday.records.agora.example.service;

import com.alibaba.fastjson.JSON;
import io.agora.rtm.*;
import io.github.notoday.records.agora.example.service.listener.IRtmChannelListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RTMConnection {

    /**
     * 客户端: 发点对点
     */
    private RtmClient client;

    /**
     * 频道
     */
    private RtmChannel channel;

    /**
     * 客户端ID(当前用户的账户名)
     */
    private final String channelName;

    /**
     * 频道名
     */
    private final String currentUserId;

    /**
     * 登陆态(单对单消息需要)
     */
    private boolean clientStatus = false;

    /**
     * 频道态(频道消息需要)
     */
    private boolean channelStatus = false;

    /**
     * 频道监听器
     */
    private final IRtmChannelListener iRtmChannelListener;
    private final RTMAuthentication rtmAuthentication;

    public RTMConnection(String appId, String channelName, String currentUserId, IRtmChannelListener iRtmChannelListener, RTMAuthentication rtmAuthentication) throws Exception {
        this.channelName = channelName;
        this.currentUserId = currentUserId;
        this.iRtmChannelListener = iRtmChannelListener;
        this.rtmAuthentication = rtmAuthentication;

        this.client = RtmClient.createInstance(appId, new RtmClientListener() {
            @Override
            public void onConnectionStateChanged(int state, int reason) {
                log.info("[RTM] 客户端-连接状态变更: {}, {}, {}", channelName, state, reason);
            }

            @Override
            public void onMessageReceived(RtmMessage message, String peerId) {
                log.debug("[RTM] 客户端-收到新的消息: {}, {}, {}", channelName, peerId, message.getText());
            }

            @Override
            public void onImageMessageReceivedFromPeer(RtmImageMessage message, String peerId) {
                log.debug("[RTM] 客户端-收到图片的消息: {}, {}, {}", channelName, peerId, message.getText());
            }

            @Override
            public void onFileMessageReceivedFromPeer(RtmFileMessage message, String peerId) {
                log.debug("[RTM] 客户端-收到文件消息: {}, {}, {}", channelName, peerId, message.getText());
            }

            @Override
            public void onMediaUploadingProgress(RtmMediaOperationProgress progress, long requestId) {
                log.debug("[RTM] 客户端-媒体上传进度: {}, {}, {}/{}", channelName, requestId, progress.currentSize, progress.totalSize);
            }

            @Override
            public void onMediaDownloadingProgress(RtmMediaOperationProgress progress, long requestId) {
                log.debug("[RTM] 客户端-媒体下载进度: {}, {}, {}/{}", channelName, requestId, progress.currentSize, progress.totalSize);
            }

            @Override
            public void onTokenExpired() {
                log.info("[RTM] 客户端-凭据已经过期: {}", channelName);

                client.renewToken(rtmAuthentication.generateRtmToken(currentUserId), new ResultCallback<Void>() {

                    @Override
                    public void onSuccess(Void responseInfo) {
                        log.info("[RTM] 客户端-刷新令牌成功: {}", channelName);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        log.error("[RTM] 客户端-刷新令牌失败: {}, {}", errorInfo.getErrorCode(), errorInfo.getErrorDescription());
                    }
                });
            }

            @Override
            public void onPeersOnlineStatusChanged(Map<String, Integer> peersStatus) {
                log.info("[RTM] 客户端-在线状态变更: {}, {}", channelName, peersStatus);
            }
        });
    }

    public void sendChannelMessage(String optionType, String msg, Object data) {
        login();
        joinGroup();

        String messageBody = buildMessageBody(optionType, msg, data);
        try {
            channel.sendMessage(client.createMessage(messageBody), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log.debug("[RTM] 推送频道消息成功: channelName: {}, text: {}", channelName, messageBody);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log.error("[RTM] 推送频道消息失败: channelName: {}, errorCode: {}, errorDesc: {}", channelName, errorInfo.getErrorCode(), errorInfo.getErrorDescription());
                }
            });
        } catch (Exception e) {
            log.error("[RTM] 推送频道消息异常: channelName: {}, optionType: {}", channelName, optionType, e);
        }
    }

    public void sendPeerMessage(String optionType, String msg, Object data, String user) {
        login();

        String messageBody = buildMessageBody(optionType, msg, data);
        try {
            client.sendMessageToPeer(user, client.createMessage(messageBody), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    log.debug("[RTM] 推送单点消息成功: user: {}, text: {}", user, messageBody);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    log.error("[RTM] 推送单点消息失败: user: {}, errorCode: {}, errorDesc: {}", user, errorInfo.getErrorCode(), errorInfo.getErrorDescription());
                }
            });
        } catch (Exception e) {
            log.error("[RTM] 推送单对单消息异常: user: {}, user: {}", user, e);
        }
    }

    public void getMembers(ResultCallback<List<RtmChannelMember>> resultCallback) {
        login();
        joinGroup();

        channel.getMembers(resultCallback);
    }

    public void release() {
        try {
            if (Objects.nonNull(channel) && channelStatus) {
                channel.leave(null);
                channel.release();
                channel = null;
            }

            if (Objects.nonNull(client) && clientStatus) {
                client.logout(null);
                client.release();
                client = null;
            }

            log.info("[RTM] 释放空闲连接: channelName: {}", channelName);
        } catch (Exception e) {
            log.error("[RTM] 释放连接异常", e);
        }
    }

    private String buildMessageBody(String optionType, String msg, Object data) {
        return JSON.toJSONString(new MessageBody(optionType, msg, data));
    }

    private void login() {
        if (clientStatus) {
            return;
        }
        synchronized (this) {
            if (clientStatus) {
                return;
            }

            final CountDownLatch loginLatch = new CountDownLatch(1);

            client.login(rtmAuthentication.generateRtmToken(currentUserId), currentUserId, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    clientStatus = true;
                    loginLatch.countDown();
                    log.info("[RTM] 登陆成功: channelName: {}", channelName);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    loginLatch.countDown();
                    log.error("[RTM] 登陆失败: channelName: {}, errorCode: {}, errorDesc: {}", channelName, errorInfo.getErrorCode(), errorInfo.getErrorDescription());
                }
            });

            try {
                if (!loginLatch.await(3, TimeUnit.SECONDS)) {
                    log.error("[RTM]: 登陆超时: channelName: {}", channelName);
                }
            } catch (InterruptedException e) {
                log.error("[RTM]: 登陆异常: channelName: {}", channelName, e);
            }
        }
    }

    private void joinGroup() {
        if (channelStatus) {
            return;
        }
        synchronized (this) {
            if (channelStatus) {
                return;
            }

            channel = client.createChannel(channelName, iRtmChannelListener);

            if (Objects.isNull(channel)) {
                log.error("[RTM]: 创建频道异常: channelName: {}", channelName);
                return;
            }

            final CountDownLatch joinGroupLatch = new CountDownLatch(1);

            channel.join(new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    channelStatus = true;
                    joinGroupLatch.countDown();
                    log.info("[RTM] 加入频道成功: channelName: {}", channelName);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    joinGroupLatch.countDown();
                    log.error("[RTM] 加入频道失败: channelName: {}, errorCode: {}, errorDesc: {}", channelName, errorInfo.getErrorCode(), errorInfo.getErrorDescription());
                }
            });

            try {
                if (!joinGroupLatch.await(3, TimeUnit.SECONDS)) {
                    log.error("[RTM]: 加入频道超时: channelName: {}", channelName);
                }
            } catch (InterruptedException e) {
                log.error("[RTM]: 加入频道异常: channelName: {}", channelName, e);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MessageBody {
        private String optionType;
        private String msg;
        private Object data;
        private Long timestamp;

        public MessageBody(String optionType, String msg, Object data) {
            this.optionType = optionType;
            this.msg = msg;
            this.data = data;
            this.timestamp = RTMThreadLocal.getTimestamp();
        }
    }
}
