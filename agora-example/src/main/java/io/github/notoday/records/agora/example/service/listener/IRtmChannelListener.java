package io.github.notoday.records.agora.example.service.listener;

import io.agora.rtm.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class IRtmChannelListener implements RtmChannelListener {

    private final String channelName;

    public IRtmChannelListener(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void onMemberCountUpdated(int memberCount) {
        log.info("[RTM] 频道-成员数量变更: channelName: {}, memberCount: {}", channelName, memberCount);
    }

    @Override
    public void onAttributesUpdated(List<RtmChannelAttribute> attributeList) {
        log.info("[RTM] 频道-属性发生变更: channelName: {}, attributes: {}", channelName, attributeList);
    }

    @Override
    public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
        log.debug("[RTM] 频道-收到新的消息: channelName: {}, user: {}, text: {}", channelName, fromMember.getUserId(), message.getText());
    }

    @Override
    public void onImageMessageReceived(RtmImageMessage message, RtmChannelMember fromMember) {
        log.debug("[RTM] 频道-收到图片消息: channelName: {}, user: {}, text: {}", channelName, fromMember.getUserId(), message.getText());
    }

    @Override
    public void onFileMessageReceived(RtmFileMessage message, RtmChannelMember fromMember) {
        log.debug("[RTM] 频道-收到文件消息: channelName: {}, user: {}, text: {}", channelName, fromMember.getUserId(), message.getText());
    }

    @Override
    public void onMemberJoined(RtmChannelMember member) {
        log.info("[RTM] 频道-用户加入频道: channelName: {}, user: {}", channelName, member.getUserId());
    }

    @Override
    public void onMemberLeft(RtmChannelMember member) {
        log.info("[RTM] 频道-用户离开频道: channelName: {}, user: {}", channelName, member.getUserId());
    }
}
