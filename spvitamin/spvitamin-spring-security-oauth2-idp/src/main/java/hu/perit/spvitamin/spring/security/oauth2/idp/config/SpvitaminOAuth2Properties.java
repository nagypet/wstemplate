/*
 * Copyright 2020-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
