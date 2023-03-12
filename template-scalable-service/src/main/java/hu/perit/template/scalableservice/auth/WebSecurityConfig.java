/*
 * Copyright 2020-2020 the original author or authors.
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

package hu.perit.template.scalableservice.auth;

import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.security.auth.SimpleHttpSecurityBuilder;
import hu.perit.spvitamin.spring.security.authprovider.localuserprovider.EnableLocalUserAuthProvider;
import hu.perit.spvitamin.spring.security.authservice.provider.AuthServiceAuthenticationProviderWithRestTemplate;
import hu.perit.template.scalableservice.rest.api.ServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * #know-how:simple-httpsecurity-builder
 *
 * @author Peter Nagy
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableLocalUserAuthProvider
public class WebSecurityConfig
{

    private final AuthServiceAuthenticationProviderWithRestTemplate authServiceAuthenticationProvider;

    @Bean
    @Order(1)
    public SecurityFilterChain configureAuthenticateEndpoint(HttpSecurity http) throws Exception
    {
        SimpleHttpSecurityBuilder.newInstance(http)
                .scope(AuthApi.BASE_URL_AUTHENTICATE + "/**")
                .authorizeRequests(r -> r.anyRequest().authenticated())
                .basicAuth()
                .jwtAuth();

        http.authenticationProvider(this.authServiceAuthenticationProvider);

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain configureTokenSecuredEndpoints(HttpSecurity http) throws Exception
    {
        SimpleHttpSecurityBuilder.newInstance(http)
                .scope(ServiceApi.BASE_URL_SERVICE + "/**")
                // we do not use secure sessions here: each endpoint has to be authenticated again and again
                .ignorePersistedSecurity()
                .authorizeRequests(r -> r.anyRequest().authenticated())
                .jwtAuth();

        return http.build();
    }
}
