/*
 * Copyright 2020-2023 the original author or authors.
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

package hu.perit.template.authservice.integration;

import feign.auth.BasicAuthRequestInterceptor;
import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.config.LocalUserProperties;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.template.authservice.api.TemplateAuthServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Peter Nagy
 */

@ActiveProfiles({"integtest"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class AuthApiIntegrationTest
{

    @Test
    void testRestEndpoint_withCorrectCredentials_whenUsing_DaoAuthenticationProvider()
    {
        log.debug("-----------------------------------------------------------------------------------------------------");
        log.debug("testRestEndpoint_withCorrectCredentials_whenUsing_DaoAuthenticationProvider()");

        try
        {
            LocalUserProperties localUserProperties = SpringContext.getBean(LocalUserProperties.class);

            TemplateAuthServiceClient templateAuthServiceClient = SimpleFeignClientBuilder.newInstance()
                    .requestInterceptor(new BasicAuthRequestInterceptor(
                            "admin", localUserProperties.getLocaluser().get("admin").getPassword()))
                    .build(TemplateAuthServiceClient.class, SysConfig.getServerProperties().getServiceUrl());

            // Calling the authentication endpoint
            AuthorizationToken authentication = templateAuthServiceClient.authenticate(null);
            log.debug(authentication.getJwt());
        }
        catch (Exception e)
        {
            log.error(StackTracer.toString(e));
            Assertions.fail(e.toString());
        }
    }


    @Test
    void testRestEndpoint_withIncorrectUsername_whenUsing_DaoAuthenticationProvider()
    {
        log.debug("-----------------------------------------------------------------------------------------------------");
        log.debug("testRestEndpoint_withIncorrectUsername_whenUsing_DaoAuthenticationProvider()");

        TemplateAuthServiceClient templateAuthServiceClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new BasicAuthRequestInterceptor(
                        "valami_nem_letezo_username", "password"))
                .build(TemplateAuthServiceClient.class, SysConfig.getServerProperties().getServiceUrl());

        // Calling the authentication endpoint
        Assertions.assertThrows(BadCredentialsException.class, () -> templateAuthServiceClient.authenticate(null));
    }


    @Test
    void testRestEndpoint_withIncorrectPassword_whenUsing_DaoAuthenticationProvider()
    {
        log.debug("-----------------------------------------------------------------------------------------------------");
        log.debug("testRestEndpoint_withIncorrectPassword_whenUsing_DaoAuthenticationProvider()");

        TemplateAuthServiceClient templateAuthServiceClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new BasicAuthRequestInterceptor(
                        "admin", "wrong_password"))
                .build(TemplateAuthServiceClient.class, SysConfig.getServerProperties().getServiceUrl());

        // Calling the authentication endpoint
        Assertions.assertThrows(BadCredentialsException.class, () -> templateAuthServiceClient.authenticate(null));
    }

}
