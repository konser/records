package io.github.notoday.records.agora.example.service.event;

import com.alibaba.fastjson.JSON;
import io.github.notoday.records.agora.example.service.RTMConnectionPool;
import io.github.notoday.records.agora.example.service.dto.RtmMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SendRtmMessageMQConsumer {

    @Resource
    private RTMConnectionPool rtmConnectionPool;

    public boolean process(RtmMessageDTO message) {
        try {
            switch (message.get_type()) {
                case CHANNEL:
                    rtmConnectionPool.sendChannelMessage(message.getChannelName(), message.getOptionType(), message.getMsg(), message.getData());
                    break;
                case ONE_TO_ONE:
                    rtmConnectionPool.sendOneToOneMessage(message.getChannelName(), message.getTargetUsers(), message.getOptionType(), message.getMsg(), message.getData());
                    break;
                case BROADCAST:
                    rtmConnectionPool.sendBroadcastMessage(message.getOptionType(), message.getMsg(), message.getData());
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            log.error("[RTM]: 推送实时消息异常: message: {}", JSON.toJSONString(message), exception);
        }

        return true;
    }
}
