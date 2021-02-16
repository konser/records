package io.github.notoday.records.agora.example.service.dto;

import io.github.notoday.records.agora.example.service.dto.enumeration.RtmMessageType;
import lombok.Data;

import java.util.List;

/**
 * @author no-today
 * @date 2021/02/01 下午2:00
 */
@Data
public class RtmMessageDTO {

    /**
     * 内部消息类型
     */
    private RtmMessageType _type;

    /**
     * 频道名(房间ID)
     */
    private String channelName;

    /**
     * 单对单消息的目标用户
     * <p>
     * 也可能用作, 发送频道消息, 但是只想指定的 "目标用户" 收到
     */
    private List<String> targetUsers;

    /**
     * 操作类型: 客户端根据该类型来分发逻辑
     */
    private String optionType;

    /**
     * 提示消息, 无需关心国际化
     */
    private String msg;

    /**
     * 携带的数据
     */
    private Object data;

    /**
     * 记录消息生成的时间戳
     */
    private long timestamp;

    private RtmMessageDTO(String channelName, List<String> targetUsers, String optionType, String msg, Object data, RtmMessageType _type) {
        this.channelName = channelName;
        this.targetUsers = targetUsers;
        this.optionType = optionType;
        this.msg = msg;
        this.data = data;
        this._type = _type;
        this.timestamp = System.currentTimeMillis();
    }

    public static RtmMessageDTO channelMessage(String channelName, String optionType, String msg, Object data) {
        return new RtmMessageDTO(channelName, null, optionType, msg, data, RtmMessageType.CHANNEL);
    }

    public static RtmMessageDTO oneToOneMessage(String channelName, List<String> targetUsers, String optionType, String msg, Object data) {
        return new RtmMessageDTO(channelName, targetUsers, optionType, msg, data, RtmMessageType.ONE_TO_ONE);
    }

    public static RtmMessageDTO broadcastMessage(String optionType, String msg, Object data) {
        return new RtmMessageDTO(null, null, optionType, msg, data, RtmMessageType.BROADCAST);
    }
}
