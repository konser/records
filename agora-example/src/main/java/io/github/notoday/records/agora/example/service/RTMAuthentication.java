package io.github.notoday.records.agora.example.service;

import io.agora.media.RtcTokenBuilder;
import io.agora.rtm.RtmTokenBuilder;
import io.github.notoday.records.agora.example.config.ApplicationProperties;
import org.springframework.stereotype.Component;

/**
 * @author no-today
 * @date 2021/01/26 下午2:54
 */
@Component
public class RTMAuthentication {

    /**
     * RTC 过期时间(秒)
     */
    public final static int EXPIRATION_TIME_IN_SECONDS = 3600;

    private final ApplicationProperties applicationProperties;

    public RTMAuthentication(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String generateRtcToken(int userId, String channelName) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + EXPIRATION_TIME_IN_SECONDS);

        return token.buildTokenWithUid(applicationProperties.getAppId(), applicationProperties.getAppCertificate(),
                channelName, userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    public String generateRtmToken(String userId) {
        RtmTokenBuilder token = new RtmTokenBuilder();
        try {
            return token.buildToken(applicationProperties.getAppId(), applicationProperties.getAppCertificate(), userId, RtmTokenBuilder.Role.Rtm_User, 0);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
