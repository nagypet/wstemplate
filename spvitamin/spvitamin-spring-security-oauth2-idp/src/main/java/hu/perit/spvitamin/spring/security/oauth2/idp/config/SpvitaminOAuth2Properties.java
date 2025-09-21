package hu.perit.spvitamin.spring.security.oauth2.idp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "spvitamin.oauth2")
@Data
public class SpvitaminOAuth2Properties
{
    private String issuer;
    private String basePath = "/api/spvitamin";
    private TokenTtl tokens = new TokenTtl();
    private List<ClientProps> clients;
    private List<String> grantTypes;


    @Data
    public static class TokenTtl
    {
        private Duration accessTtl = Duration.ofMinutes(5);
        private Duration refreshTtl = Duration.ofDays(30);
    }


    @Data
    public static class ClientProps
    {
        private String clientId;
        private String clientSecret;
        private Set<String> scopes;
        private Map<String, Object> extra;
        private boolean allowRefreshTokenInResponse = false;
    }
}
