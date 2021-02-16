package io.github.notoday.records.agora.example.web.rest;

import io.github.notoday.records.agora.example.service.RTMAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author no-today
 * @date 2021/01/26 下午3:43
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AgoraAuthenticationResource {

    private final RTMAuthentication rtmAuthentication;

    public AgoraAuthenticationResource(RTMAuthentication rtmAuthentication) {
        this.rtmAuthentication = rtmAuthentication;
    }

    /**
     * 生成 Token
     *
     * @param ignoreRtm 忽略RTM token
     */
    @GetMapping(path = {"/agora/generateToken", "/feign/agora/generateToken"})
    public ResponseEntity<Map<String, Object>> generateToken(String channelName, Boolean ignoreRtm, String currentUser) {
//        String currentUser = SecurityUtils.getCurrentUserLogin().get();

        log.debug("[agora]: 刷新令牌: user: {}, channel: {}", currentUser, channelName);

        Map<String, Object> data = new HashMap<>((int) Math.ceil(4 / .75));
        if (Objects.nonNull(channelName) && !channelName.isEmpty()) {
            data.put("rtcToken", rtmAuthentication.generateRtcToken(Integer.parseInt(currentUser), channelName));
            data.put("rtcTokenExpire", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(RTMAuthentication.EXPIRATION_TIME_IN_SECONDS));
        }
        if (Objects.isNull(ignoreRtm) || !ignoreRtm) {
            data.put("rtmToken", rtmAuthentication.generateRtmToken(currentUser));
            data.put("rtmTokenExpire", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
        }

        return ResponseEntity.ok(data);
    }
}
