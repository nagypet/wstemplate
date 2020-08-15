package hu.perit.performancetester.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tester")
public class TesterProperties {

    private int threadCount = 30;
    private int batchSize = 1000;
    private int durationMins = 10;
    private int pauseSeconds = 10;
    private String authServiceUrl;
    private String scalableServiceUrl;
}
