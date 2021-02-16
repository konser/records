package io.github.notoday.records.agora.example;

import io.github.notoday.records.agora.example.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class AgoraExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgoraExampleApplication.class, args);
    }

}
