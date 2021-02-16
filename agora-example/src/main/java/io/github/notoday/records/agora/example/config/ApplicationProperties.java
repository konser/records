package io.github.notoday.records.agora.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    /**
     * 声网 APP_ID
     */
    private String appId;
    private String appCertificate;

    /**
     * RTM 连接池初始大小
     */
    private Integer rtmInitConnectionPoolSize = 50;

    /**
     * RTM 连接池最大大小
     */
    private Integer rtmMaxConnectionPoolSize = 5000;
}
